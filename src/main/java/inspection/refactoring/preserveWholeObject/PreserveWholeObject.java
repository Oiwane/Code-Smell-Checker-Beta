package inspection.refactoring.preserveWholeObject;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import inspection.refactoring.RefactoringUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PreserveWholeObject implements LocalQuickFix {
  @Nls(capitalization = Nls.Capitalization.Sentence)
  @NotNull
  @Override
  public String getFamilyName() {
    return "Preserve whole Object";
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    PsiElement element = descriptor.getPsiElement();
    PsiParameterList parameterList = (PsiParameterList) element;
    PsiMethod method = (PsiMethod) parameterList.getParent();
    PsiClass psiClass = method.getContainingClass();
    PsiMethod[] methodsContainsClass = psiClass.getAllMethods();

    PsiReference[] referenceResults = MethodReferencesSearch.search(method).toArray(new PsiReference[0]);

    for (PsiReference reference : referenceResults) {
      PsiMethodCallExpression methodCallExpression = ((PsiMethodCallExpression) reference.getElement().getParent());
      PsiExpression[] arguments = methodCallExpression.getArgumentList().getExpressions();

      Map<PsiElement, List<ArgumentInfo>> map = new HashMap<>();
      for (int i = 0; i < arguments.length; i++) {
        PsiExpression argument = arguments[i];
        // 引数がメソッド呼び出しになっているかを確認する
        if (argument instanceof PsiMethodCallExpression) {
          addArgumentInfo(map, i, argument);
        }
        // TODO オブジェクトがどこで宣言されたのかを確認する
        // TODO 制約 : 同じスコープ内の変数（オブジェクト）しか見ない
        else if (argument instanceof PsiReferenceExpression) {
          PsiCodeBlock scope = RefactoringUtil.findCodeBlockInParents(methodCallExpression);
          PsiMethodCallExpression argumentMethod = findTargetElementInScope(scope, argument);
          if (argumentMethod != null) addArgumentInfo(map, i, argumentMethod);
        }
      }

//      for (PsiElement key : map.keySet()) {
//        if (map.get(key).size() > 1) {
//          addParameter(map, key);
//
//          for (ArgumentInfo argumentInfo : map.get(key)) {
//            PsiParameter targetParameter = parameterList.getParameters()[argumentInfo.getIndex()];
//            RefactoringUtil.optimiseParameter(targetParameter, argumentInfo.getArgumentMethod());
//          }
//        }
//      }

      PsiMethod newMethod = cloneMethod(method);

      createParameterList(newMethod.getParameterList(), map);

//      if (existsSameMethod(newMethod, methodsContainsClass)) continue;
      System.out.println(existsSameMethod(newMethod, methodsContainsClass));

//      psiClass.add(newMethod);
    }

  }

  private void addArgumentInfo(@NotNull Map<PsiElement, List<ArgumentInfo>> map, int index, PsiExpression argumentMethod) {
    PsiElement key = extractObjectCallingMethod(argumentMethod).getReference().resolve();

    if (!map.containsKey(key)) {
      map.put(key, new ArrayList<>());
    }
    map.get(key).add(new ArgumentInfo(index, ((PsiMethodCallExpression) argumentMethod)));
  }

  /**
   * オブジェクトを特定する
   *
   * @param expression PsiExpression
   * @return 最終的にはPsiReferenceExpressionが戻ってくる
   */
  @Nullable
  private PsiExpression extractObjectCallingMethod(@NotNull PsiExpression expression) {
    for (PsiElement childElement : expression.getChildren()) {
      if (childElement instanceof PsiExpression) {
        return extractObjectCallingMethod((PsiExpression) childElement);
      }
    }

    return expression;
  }

  @Nullable
  private PsiMethodCallExpression findTargetElementInScope(@NotNull PsiCodeBlock scope, PsiElement targetElement) {
    PsiMethodCallExpression returnValue = null;
    for (PsiStatement statement : scope.getStatements()) {
      if (statement instanceof PsiDeclarationStatement) {
        returnValue = findTargetElementInStatement((PsiDeclarationStatement) statement, targetElement);
      } else if (statement instanceof PsiExpressionStatement) {
        returnValue = findTargetElementInStatement((PsiExpressionStatement) statement, targetElement);
      }
      if (returnValue != null) return returnValue;
    }

    return null;
  }

  @Nullable
  private PsiMethodCallExpression findTargetElementInStatement(@NotNull PsiDeclarationStatement statement, @NotNull PsiElement targetElement) {
    PsiMethodCallExpression returnValue = null;
    final PsiReference reference = targetElement.getReference();
    assert reference != null;

    // int a = b = c.hoge(); みたいな記述は無視
    for (PsiElement element : statement.getDeclaredElements()) {
      if (element instanceof PsiLocalVariable) {
        for (PsiElement child : element.getChildren()) {
          if (child instanceof PsiMethodCallExpression) {
            returnValue = (PsiMethodCallExpression) child;
          }
        }
      }
    }

    return (statement.getTextRange().equals(reference.resolve().getTextRange())) ? returnValue : null;
  }

  @Nullable
  private PsiMethodCallExpression findTargetElementInStatement(@NotNull PsiExpressionStatement statement, PsiElement targetElement) {
    PsiExpression expression = statement.getExpression();
    PsiReferenceExpression compareVariable = null;
    PsiMethodCallExpression returnValue = null;
    for (PsiElement element : expression.getChildren()) {
      if (element instanceof PsiReferenceExpression) {
        compareVariable = (PsiReferenceExpression) element;
      } else if (element instanceof PsiMethodCallExpression) {
        returnValue = (PsiMethodCallExpression) element;
      }
    }

    if (compareVariable == null) return null;
    assert compareVariable.getReference() != null;
    assert targetElement.getReference() != null;
    return (compareVariable.getReference().resolve().equals(targetElement.getReference().resolve())) ? returnValue : null;
  }

  private PsiMethod cloneMethod(@NotNull PsiMethod originalMethod) {
    PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(originalMethod.getProject());
    PsiMethod newMethod = factory.createMethod(originalMethod.getName(), originalMethod.getReturnType(), originalMethod.getContainingClass());
    PsiParameterList originalParameterList = originalMethod.getParameterList();

    // PsiCodeBlockの作成
    PsiCodeBlock codeBlock = cloneCodeBlock(originalMethod, factory, newMethod);
    // PsiParameterListの作成
    PsiParameterList newParameterList = cloneParameterList(originalParameterList, factory);

    newMethod.getModifierList().replace(originalMethod.getModifierList());
    newMethod.getReturnTypeElement().replace(originalMethod.getReturnTypeElement());
    newMethod.getParameterList().replace(newParameterList);
    newMethod.getThrowsList().replace(originalMethod.getThrowsList());
    newMethod.getBody().replace(codeBlock);

    return newMethod;
  }

  @NotNull
  private PsiCodeBlock cloneCodeBlock(@NotNull PsiMethod originalMethod, @NotNull PsiElementFactory factory, PsiMethod newMethod) {
    String bodyStr = originalMethod.getBody().getText();
    return factory.createCodeBlockFromText(bodyStr, newMethod);
  }

  @NotNull
  private PsiParameterList cloneParameterList(@NotNull PsiParameterList originalParameterList, PsiElementFactory factory) {
    String[] newParametersName = new String[originalParameterList.getParametersCount()];
    List<PsiType> newType = new ArrayList<>();
    for (int i = 0; i < originalParameterList.getParametersCount(); i++) {
      newParametersName[i] = originalParameterList.getParameters()[i].getName();
      newType.add(i, originalParameterList.getParameters()[i].getType());
    }
    return factory.createParameterList(newParametersName, newType.toArray(new PsiType[0]));
  }

  private void createParameterList(PsiParameterList newParameterList, @NotNull Map<PsiElement, List<ArgumentInfo>> map) {
    for (PsiElement key : map.keySet()) {
      if (map.get(key).size() > 1) {
        addParameter(map, key, newParameterList);

        for (ArgumentInfo argumentInfo : map.get(key)) {
          PsiParameter targetParameter = newParameterList.getParameters()[argumentInfo.getIndex()];
          RefactoringUtil.optimiseParameter(targetParameter, argumentInfo.getArgumentMethod());
        }
      }
    }
  }

  /**
   * パラメータを追加する
   *
   * @brief パラメータの一番最後に任意のオブジェクト用のパラメータを追加する
   * @param map 追加するパラメータの対象となるオブジェクトを格納しているマップ
   * @param key mapの添え字
   */
  private void addParameter(@NotNull Map<PsiElement, List<ArgumentInfo>> map, PsiElement key, @NotNull PsiParameterList newParameterList) {
    PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(newParameterList.getProject());
//    PsiParameter finalParameter = newParameterList.getParameters()[newParameterList.getParametersCount() - 1];
//    Project project = newParameterList.getProject();
//    Document document = PsiDocumentManager.getInstance(project).getDocument(newParameterList.getContainingFile());
    if (map.containsKey(key)) {
      if (!(key instanceof PsiVariable)) return;
//      if (key instanceof PsiField) {
//        type = ((PsiField) key).getType();
//      } else if (key instanceof PsiLocalVariable) {
//        type = ((PsiLocalVariable) key).getType();
//      }
      PsiVariable variable = (PsiVariable) key;
      PsiParameter newParameter = factory.createParameter(variable.getName(), variable.getType());

      newParameterList.add(newParameter);
//      String typeStr = type.toString().substring(("PsiType:").length());
//      String variableName = ((PsiVariable) key).getName();
//      String insertedString = ", " + typeStr + " " + variableName;
//
//      document.insertString(finalParameter.getTextRange().getEndOffset(), insertedString);
//      PsiDocumentManager.getInstance(project).commitDocument(document);
    } else {
      System.out.println("null");
    }
  }

  private boolean existsSameMethod(PsiMethod target, PsiMethod[] samples) {
    for (PsiMethod sample : samples) {
      if (!target.getName().equals(sample.getName())) continue;

      List<PsiParameter> targetParameters = Arrays.asList(target.getParameterList().getParameters());
      List<PsiParameter> sampleParameters = Arrays.asList(sample.getParameterList().getParameters());
      if (targetParameters.size() != sampleParameters.size()) continue;

      for (int i = 0; i < targetParameters.size(); i++) {
        for (int j = 0; j < sampleParameters.size(); j++) {
          if (targetParameters.get(i).getTypeElement().equals(sampleParameters.get(j).getTypeElement())) {
            targetParameters.remove(i);
            sampleParameters.remove(j);
            break;
          }
        }
      }

      if (targetParameters.size() == 0) return true;
    }

    return false;
  }
}
