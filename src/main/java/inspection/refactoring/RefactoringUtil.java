package inspection.refactoring;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.*;
import com.intellij.refactoring.safeDelete.SafeDeleteHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RefactoringUtil<E> {
  /**
   * 三次元リストの初期化をする
   *
   * @param list 初期化をする三次元リスト
   * @param first 三次元目のリストサイズ
   * @param second 二次元目のリストサイズ
   */
  public void initThreeDimensionalList(List<List<List<E>>> list, int first, int second) {
    for(int i = 0; i < first; i++){
      list.add(new ArrayList<>());
      for(int j = 0;j < second; j++){
        list.get(i).add(new ArrayList<>());
      }
    }
  }

  /**
   * targetParameterをnewElementに置き換えてtargetParameterを削除する
   *
   * @param targetParameter 削除対象のパラメータ
   * @param newElement targetParameterと置き換えるメソッド呼び出し
   */
  public static void optimiseParameter(PsiParameter targetParameter, PsiMethodCallExpression newElement) {
    RefactoringUtil.replaceParameterObject(targetParameter, newElement);
    RefactoringUtil.deleteUnnecessaryParameter(targetParameter);
  }

  /**
   * 使用しているパラメータをメソッド呼び出しに置き換える
   *
   * @param targetParameter 対象のパラメータ
   * @param newElement パラメータの代わりに置くメソッド呼び出し
   */
  private static void replaceParameterObject(PsiParameter targetParameter, PsiMethodCallExpression newElement) {
    List<PsiReferenceExpression> referenceExpressionList = new ArrayList<>();

    findReplacedElements(referenceExpressionList, targetParameter);

    for (PsiReference reference : referenceExpressionList) {
      PsiReferenceExpression referenceExpression;
      if (reference.getElement() instanceof PsiReferenceExpression) {
        referenceExpression = ((PsiReferenceExpression) reference.getElement());
      } else if (reference.getElement() instanceof PsiIdentifier) {
        referenceExpression = (PsiReferenceExpression) reference.getElement().getParent();
      } else continue;
      ApplicationManager.getApplication().invokeLater(() -> {
        WriteCommandAction.runWriteCommandAction(targetParameter.getProject(), () -> {
          referenceExpression.replace(newElement);
        });
      });
    }
  }

  private static void findReplacedElements(List<PsiReferenceExpression> referenceExpressionList, @NotNull PsiParameter targetParameter) {
    PsiParameterList parameterList = ((PsiParameterList) targetParameter.getParent());
    PsiCodeBlock codeBlock = ((PsiMethod) parameterList.getParent()).getBody();

    assert codeBlock != null;
    findReplacedElements(referenceExpressionList, targetParameter, codeBlock);
  }

  private static void findReplacedElements(List<PsiReferenceExpression> referenceExpressionList, PsiParameter targetParameter, @NotNull PsiElement element) {
    for (PsiElement childElement : element.getChildren()) {
      findReplacedElements(referenceExpressionList, targetParameter, childElement);
      if (childElement instanceof PsiReferenceExpression) {
        PsiReferenceExpression referenceExpression = ((PsiReferenceExpression) childElement);
        if (referenceExpression.resolve().getTextRange().equals(targetParameter.getTextRange())) {
          referenceExpressionList.add(referenceExpression);
        }
      }
    }
  }

  private static void deleteUnnecessaryParameter(PsiParameter parameter) {
    ApplicationManager.getApplication().invokeLater(() -> SafeDeleteHandler.invoke(parameter.getProject(), new PsiElement[]{parameter}, true));
//    SafeDeleteHandler.invoke(parameter.getProject(), new PsiElement[]{parameter}, true);
  }

  /**
   * elementの存在するスコープを探す
   *
   * @param element 対象のPsiElement
   * @return elementの存在するスコープ
   */
  public static PsiCodeBlock findCodeBlockInParents(@NotNull PsiElement element) {
    PsiElement parentElement = element.getParent();
    if (parentElement instanceof PsiCodeBlock) return (PsiCodeBlock) parentElement;
    else return findCodeBlockInParents(parentElement);
  }
}
