package inspection.psi;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

/**
 * PSI要素の操作に関するクラス
 */
public class PsiUtil {
  public static int countStatement(@NotNull PsiMethod method) {
    if (method.getBody() == null) return 0;

    return countStatement(method.getBody());
  }

  private static int countStatement(@NotNull PsiCodeBlock codeBlock) {
    int count = 0;

    for (PsiStatement statement : codeBlock.getStatements()) {
      count += countStatement(statement) + countStatementInStatement(statement);
    }

    return count + codeBlock.getStatementCount();
  }

  private static int countStatement(@NotNull PsiElement parentElement) {
    int count = 0;

    for (PsiElement element : parentElement.getChildren()) {
      if (element instanceof PsiCodeBlock) {
        count += countStatement((PsiCodeBlock) element);
      }
      else{
        count += countStatement(element);
      }
    }

    return count;
  }

  private static int countStatementInStatement(@NotNull PsiStatement statement) {
    int count = 0;

    for (PsiElement element : statement.getChildren()) {
      if (element instanceof PsiStatement && !(element instanceof PsiBlockStatement)) {
        count += countStatementInStatement((PsiStatement) element) + 1;
      }
    }

    return count;
  }

  public static int countPsiMethodCallExpression(@NotNull PsiMethodCallExpression expression) {
    int count = 1;

    for (PsiElement element : expression.getChildren()) {
      if (element instanceof PsiReferenceExpression) {
        count += countPsiMethodCallExpression((PsiReferenceExpression) element);
      }
    }

    return count;
  }

  private static int countPsiMethodCallExpression(@NotNull PsiReferenceExpression expression) {
    int count = 0;

    for (PsiElement element : expression.getChildren()) {
      if (element instanceof PsiMethodCallExpression) {
        count += countPsiMethodCallExpression((PsiMethodCallExpression) element);
      }
    }

    return count;
  }
}
