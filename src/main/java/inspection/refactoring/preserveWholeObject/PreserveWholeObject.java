package inspection.refactoring.preserveWholeObject;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import inspection.refactoring.RefactoringUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PreserveWholeObject implements LocalQuickFix {
  private Project myProject;
  private PsiParameterList parameterList;

  @Nls(capitalization = Nls.Capitalization.Sentence)
  @NotNull
  @Override
  public String getFamilyName() {
    return "Preserve whole Object";
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    myProject = project;
    PsiElement element = descriptor.getPsiElement();
    parameterList = (PsiParameterList) element;
    PsiMethod method = (PsiMethod) parameterList.getParent();

    PsiReference[] referenceResults = MethodReferencesSearch.search(method).toArray(new PsiReference[0]);

    for (PsiReference reference : referenceResults) {
      PsiMethodCallExpression methodCallExpression = ((PsiMethodCallExpression) reference.getElement().getParent());
      PsiExpression[] arguments = methodCallExpression.getArgumentList().getExpressions();

      Map<PsiElement, List<ArgumentInfo>> map = new HashMap<>();
      for (int i = 0; i < arguments.length; i++) {
        PsiExpression argument = arguments[i];
        // 引数がメソッド呼び出しになっているかを確認する
        if (argument instanceof PsiMethodCallExpression) {
          PsiElement key = extractObjectCallingMethod(argument).getReference().resolve();

          if (!map.containsKey(key)) {
            map.put(key, new ArrayList<>());
          }
          map.get(key).add(new ArgumentInfo(i, ((PsiMethodCallExpression) argument)));
        }
        // TODO オブジェクトがどこで宣言されたのかを確認する
//        else if (argument instanceof PsiReferenceExpression) {
//          System.out.println("PsiReferenceExpression : " + argument.getText());
//
//        }
      }

      for (PsiElement key : map.keySet()) {
        if (map.get(key).size() > 1) {
          addParameter(map, key);

          for (ArgumentInfo argumentInfo : map.get(key)) {
            PsiParameter targetParameter = parameterList.getParameters()[argumentInfo.getIndex()];
            RefactoringUtil.replaceParameterObject(argumentInfo.getArgumentMethod(), targetParameter);
            RefactoringUtil.deleteUnnecessaryParameter(targetParameter);
          }
        }
      }
    }

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

  /**
   * パラメータを追加する
   *
   * @brief パラメータの一番最後に任意のオブジェクト用のパラメータを追加する
   * @param map 追加するパラメータの対象となるオブジェクトを格納しているマップ
   * @param key mapの添え字
   */
  private void addParameter(@NotNull Map<PsiElement, List<ArgumentInfo>> map, PsiElement key) {
    PsiParameter finalParameter = parameterList.getParameters()[parameterList.getParametersCount() - 1];
    Document document = PsiDocumentManager.getInstance(myProject).getDocument(parameterList.getContainingFile());
    if (map.containsKey(key)) {
      PsiType type = PsiType.NULL;
      if (key instanceof PsiField) {
        type = ((PsiField) key).getType();
      } else if (key instanceof PsiLocalVariable) {
        type = ((PsiLocalVariable) key).getType();
      }
      assert type != PsiType.NULL;
      String typeStr = type.toString().substring(("PsiType:").length());
      String variableName = ((PsiVariable) key).getName();
      String insertedString = ", " + typeStr + " " + variableName;

      document.insertString(finalParameter.getTextRange().getEndOffset(), insertedString);
      PsiDocumentManager.getInstance(myProject).commitDocument(document);
    } else {
      System.out.println("null");
    }
  }
}
