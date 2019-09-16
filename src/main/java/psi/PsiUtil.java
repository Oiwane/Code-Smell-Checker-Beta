package psi;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public class PsiUtil {
  public static int countStatement(@NotNull PsiMethod method) {
    if (method.getBody() == null) return 0;

    return countStatement(method.getBody());
  }

  private static int countStatement(@NotNull PsiCodeBlock codeBlock) {
    int count = 0;
    boolean existsStatement;

    for (PsiStatement statement : codeBlock.getStatements()) {
      existsStatement = false;
      for (PsiElement element : statement.getChildren()) {
        if (element instanceof PsiStatement) {
          count += countStatement((PsiStatement) element);
          existsStatement = true;
        }
      }

      if (!existsStatement) count++;
    }

    return count;
  }

  private static int countStatement(@NotNull PsiStatement statement) {
    int count = 0;
    boolean existsCodeBlock = false;
    boolean existsStatement = false;

    for (PsiElement element : statement.getChildren()) {
      if (element instanceof PsiCodeBlock) {
        count += countStatement((PsiCodeBlock) element);
        existsCodeBlock = true;
      }
      else if (element instanceof PsiStatement) {
        count += countStatement((PsiStatement) element);
        existsStatement = true;
      }

    }

    if (!(existsCodeBlock || existsStatement)) count++;

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
