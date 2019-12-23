package inspection.refactoring;

import com.intellij.psi.*;
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
   * @param method 変更するメソッド
   * @param targetParameter 削除対象のパラメータ
   * @param newElement targetParameterと置き換えるメソッド呼び出し
   */
  public static void optimiseParameter(PsiMethod method, PsiParameter targetParameter, PsiMethodCallExpression newElement) {
    RefactoringUtil.replaceParameterObject(method, targetParameter, newElement);
    targetParameter.delete();
  }

  /**
   * 使用しているパラメータをメソッド呼び出しに置き換える
   *
   * @param method 変更するメソッド
   * @param targetParameter 対象のパラメータ
   * @param newElement パラメータの代わりに置くメソッド呼び出し
   */
  private static void replaceParameterObject(@NotNull PsiMethod method, @NotNull PsiParameter targetParameter, PsiMethodCallExpression newElement) {
    PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(targetParameter.getProject());
    PsiDeclarationStatement declarationStatement = factory.createVariableDeclarationStatement(targetParameter.getName(), targetParameter.getType(), newElement);

    if (method.getBody().getStatementCount() != 0) {
      PsiStatement firstStatement = method.getBody().getStatements()[0];
      method.getBody().addBefore(declarationStatement, firstStatement);
    } else {
      method.getBody().add(declarationStatement);
    }
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