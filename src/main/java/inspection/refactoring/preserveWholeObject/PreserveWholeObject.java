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
    List<PsiMethod> methodForCompare = new ArrayList<>();

    PsiReference[] referenceResults = MethodReferencesSearch.search(method).toArray(new PsiReference[0]);

    for (PsiReference reference : referenceResults) {
      PsiMethodCallExpression methodCallExpression = ((PsiMethodCallExpression) reference.getElement().getParent());
      PsiExpressionList argumentList = methodCallExpression.getArgumentList();
      PsiExpression[] arguments = argumentList.getExpressions();

      Map<PsiElement, List<ArgumentInfo>> map = new HashMap<>();

      extractArgumentInfo(methodCallExpression, arguments, map);
      PsiMethod newMethod = cloneMethod(method);
      createParameterList(newMethod, map);

      // メソッドの呼び出し先の編集
      changeArgumentList(map, methodCallExpression);

      if (existsSameMethod(newMethod, methodsContainsClass) || existsSameMethodInOtherNewMethod(methodForCompare, newMethod)) continue;

      psiClass.add(newMethod);
      methodForCompare.add(newMethod);
      PsiDocumentManager.getInstance(project).commitAllDocuments();

    }

  }

  private void changeArgumentList(@NotNull Map<PsiElement, List<ArgumentInfo>> map, @NotNull PsiMethodCallExpression methodCallExpression) {
    Project project = methodCallExpression.getProject();
    PsiExpressionList argumentList = methodCallExpression.getArgumentList();
    PsiExpression[] arguments = argumentList.getExpressions();

    for (PsiElement key : map.keySet()) {
      if (map.get(key).size() > 1) {
        PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(project);
        if (map.containsKey(key)) {
          if (!(key instanceof PsiVariable)) return;
          PsiVariable variable = (PsiVariable) key;
          PsiExpression newArgument = factory.createExpressionFromText(variable.getNameIdentifier().getText(), argumentList);
          argumentList.add(newArgument);
        } else {
          System.out.println("null");
        }

        List<PsiExpression> targetArguments = new ArrayList<>();
        for (ArgumentInfo argumentInfo : map.get(key)) {
          PsiExpression targetArgument = arguments[argumentInfo.getIndex()];
          targetArguments.add(targetArgument);
        }

        for (PsiExpression deleteArgument : targetArguments) {
          deleteArgument.delete();
        }
      }
    }
  }

  private void extractArgumentInfo(PsiMethodCallExpression methodCallExpression, @NotNull PsiExpression[] arguments, Map<PsiElement, List<ArgumentInfo>> map) {
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
  }

  private boolean existsSameMethodInOtherNewMethod(@NotNull List<PsiMethod> methodForCompare, PsiMethod newMethod) {
    for (PsiMethod comparedMethod : methodForCompare) {
      if (comparedMethod.getText().equals(newMethod.getText())) return true;
    }

    return false;
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
        returnValue = findTargetElementInStatement(scope, (PsiDeclarationStatement) statement, targetElement);
      } else if (statement instanceof PsiExpressionStatement) {
        returnValue = findTargetElementInStatement(scope, (PsiExpressionStatement) statement, targetElement);
      }
      if (returnValue != null) return returnValue;
    }

    return null;
  }

  @Nullable
  private PsiMethodCallExpression findTargetElementInStatement(PsiCodeBlock scope, @NotNull PsiDeclarationStatement statement, @NotNull PsiElement targetElement) {
    PsiMethodCallExpression returnValue = null;
    final PsiReference reference = targetElement.getReference();
    if (reference == null) return null;

    // int a = b = c.hoge(); みたいな記述は無視
    for (PsiElement element : statement.getDeclaredElements()) {
      if (element instanceof PsiLocalVariable) {
        for (PsiElement child : element.getChildren()) {
          if (child instanceof PsiMethodCallExpression) {
            returnValue = (PsiMethodCallExpression) child;
          } else if (child instanceof PsiReferenceExpression) {
            PsiElement variableReference = child.getReference().resolve();
            return findTargetElementInScope(scope, variableReference);
          }
        }
      }
    }

    return (statement.getTextRange().equals(reference.resolve().getTextRange())) ? returnValue : null;
  }

  @Nullable
  private PsiMethodCallExpression findTargetElementInStatement(PsiCodeBlock scope, @NotNull PsiExpressionStatement statement, PsiElement targetElement) {
    PsiExpression expression = statement.getExpression();
    PsiReferenceExpression compareVariable = null;
    PsiMethodCallExpression returnValue = null;
    boolean appearEQ = false;
    for (PsiElement element : expression.getChildren()) {
      if (element instanceof PsiReferenceExpression) {
        if (appearEQ) {
          return findTargetElementInScope(scope, element);
        } else {
          compareVariable = (PsiReferenceExpression) element;
        }
      } else if (element instanceof PsiMethodCallExpression) {
        returnValue = (PsiMethodCallExpression) element;
      } else if (element instanceof PsiJavaToken) {
        PsiJavaToken javaToken = (PsiJavaToken) element;
        appearEQ = javaToken.getText().equals("=");
      }
    }

    if (compareVariable == null) return null;
    assert compareVariable.getReference() != null;
    if (targetElement.getReference() == null) return null;
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

  private void createParameterList(@NotNull PsiMethod newMethod, @NotNull Map<PsiElement, List<ArgumentInfo>> map) {
    PsiParameterList newParameterList = newMethod.getParameterList();
    for (PsiElement key : map.keySet()) {
      if (map.get(key).size() > 1) {
        addParameter(map, key, newParameterList);

        List<PsiParameter> parameters = new ArrayList<>();
        for (ArgumentInfo argumentInfo : map.get(key)) {
          PsiParameter targetParameter = newParameterList.getParameters()[argumentInfo.getIndex()];
          parameters.add(targetParameter);
        }

        ArgumentInfo[] argumentInfo = map.get(key).toArray(new ArgumentInfo[0]);
        for (int i = 0; i < argumentInfo.length; i++) {
          RefactoringUtil.optimiseParameter(newMethod, parameters.get(i), argumentInfo[i].getArgumentMethod());
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
   * @param newParameterList 新しいパラメータリスト
   */
  private void addParameter(@NotNull Map<PsiElement, List<ArgumentInfo>> map, PsiElement key, @NotNull PsiParameterList newParameterList) {
    PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(newParameterList.getProject());
    if (map.containsKey(key)) {
      if (!(key instanceof PsiVariable)) return;
      PsiVariable variable = (PsiVariable) key;
      PsiParameter newParameter = factory.createParameter(variable.getName(), variable.getType());

      newParameterList.add(newParameter);
    } else {
      System.out.println("null");
    }
  }

  private boolean existsSameMethod(PsiMethod target, @NotNull PsiMethod[] samples) {
    for (PsiMethod sample : samples) {
      if (!target.getName().equals(sample.getName())) continue;
      if (target.getParameterList().getParametersCount() != sample.getParameterList().getParametersCount()) continue;

      PsiParameter[] targetParameters = target.getParameterList().getParameters();
      PsiParameter[] sampleParameters = sample.getParameterList().getParameters();

      if (isSameParameters(targetParameters, sampleParameters)) return true;
    }

    return false;
  }

  private boolean isSameParameters(@NotNull PsiParameter[] targetParameters, PsiParameter[] sampleParameters) {
    int counter = 0;
    boolean[] flags = new boolean[targetParameters.length];
    for (int i = 0; i < flags.length; i++) {
      flags[i] = false;
    }
    for (PsiParameter targetParameter : targetParameters) {
      for (int j = 0; j < sampleParameters.length; j++) {
        if (targetParameter.getType().equals(sampleParameters[j].getType()) && !flags[j]) {
          flags[j] = true;
          counter++;
          break;
        }
      }
    }

    return counter == targetParameters.length;
  }
}
