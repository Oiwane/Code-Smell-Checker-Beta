package inspection.refactoring;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
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
      System.out.println(condition.getText());
    }
    System.out.println();
  }

  private void findPsiIfStatement(@NotNull PsiCodeBlock codeBlock) {
    for (PsiStatement statement : codeBlock.getStatements()) {
      if (statement instanceof PsiIfStatement) {
        final PsiIfStatement ifStatement = (PsiIfStatement) statement;
        ifStatementList.add(ifStatement);
        for (PsiElement child : statement.getChildren()) {
          if (child instanceof PsiCodeBlock) {
            findPsiIfStatement((PsiCodeBlock) child);
            break;
          }
        }
      } else if (statement instanceof PsiBlockStatement){
        PsiBlockStatement blockStatement = (PsiBlockStatement) statement;
        findPsiIfStatement(blockStatement.getCodeBlock());
      } else if (statement instanceof PsiLoopStatement) {
        PsiLoopStatement loopStatement = (PsiLoopStatement) statement;
        if (loopStatement.getBody() == null) continue;
        if (loopStatement.getBody() instanceof PsiBlockStatement) {
          findPsiIfStatement(((PsiBlockStatement) loopStatement.getBody()).getCodeBlock());
        }
      }
    }
  }

}
