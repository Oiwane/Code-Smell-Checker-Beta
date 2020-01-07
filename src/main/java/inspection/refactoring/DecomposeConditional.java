package inspection.refactoring;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiIfStatement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPolyadicExpression;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.tree.IElementType;
import com.intellij.refactoring.extractMethod.ExtractMethodHandler;
import com.intellij.refactoring.extractMethod.ExtractMethodProcessor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DecomposeConditional implements LocalQuickFix {
  private List<PsiIfStatement> ifStatementList;

  @Nls(capitalization = Nls.Capitalization.Sentence)
  @NotNull
  @Override
  public String getFamilyName() {
    return "Decompose conditional";
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    PsiMethod method = (PsiMethod) descriptor.getPsiElement().getParent();
    PsiCodeBlock codeBlock = method.getBody();
    ifStatementList = new ArrayList<>();

    if (codeBlock == null) return;
    findPsiIfStatement(codeBlock);

    for (PsiIfStatement ifStatement : ifStatementList) {
      PsiExpression condition = ifStatement.getCondition();

      IElementType elementType;
      if (isPsiBinaryExpression(condition)) {
        PsiBinaryExpression binaryExpression = (PsiBinaryExpression) condition;
        elementType = binaryExpression.getOperationTokenType();
      } else if (isPsiPolyadicExpression(condition)) {
        PsiPolyadicExpression polyadicExpression = (PsiPolyadicExpression) condition;
        elementType = polyadicExpression.getOperationTokenType();
      } else continue;

      if (hasMultipleConditionalExpression(elementType)) {
        PsiElement[] elements = new PsiElement[]{condition};
        ExtractMethodProcessor processor = ExtractMethodHandler.getProcessor(project, elements, method.getContainingFile(), false);
        assert processor != null;

        ApplicationManager.getApplication().invokeLater(() -> {
          ExtractMethodHandler.invokeOnElements(project, processor, method.getContainingFile(), true);
        });
      }
    }
  }

  private void findPsiIfStatement(@NotNull PsiCodeBlock codeBlock) {
    for (PsiStatement statement : codeBlock.getStatements()) {
      findPsiIfStatement(statement);
    }
  }

  private void findPsiIfStatement(@NotNull PsiElement element) {
    for (PsiElement child : element.getChildren()) {
      findPsiIfStatement(child);
      if (child instanceof PsiIfStatement) ifStatementList.add((PsiIfStatement) child);
    }
  }

  private boolean isPsiBinaryExpression(PsiElement element) {
    return element instanceof PsiBinaryExpression;
  }

  private boolean isPsiPolyadicExpression(PsiElement element) {
    return element instanceof PsiPolyadicExpression;
  }

  private boolean hasMultipleConditionalExpression(IElementType elementType) {
    return elementType.equals(JavaTokenType.OROR) || elementType.equals(JavaTokenType.ANDAND);
  }
}
