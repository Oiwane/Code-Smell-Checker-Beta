package inspection.MessageChains;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static inspection.InspectionSetting.DEFAULT_NUM_CHAINS;
import static inspection.InspectionSetting.GROUP_NAME;

public class MessageChainsInspection extends AbstractBaseJavaLocalInspectionTool {
  private LocalQuickFix quickFix = new MessageChainsFix();
  private int numChains;

  public MessageChainsInspection() {
    numChains = DEFAULT_NUM_CHAINS;
  }

  @Override
  @NotNull
  public String getDisplayName() {
      return "Message chains";
  }

  @NonNls
  @NotNull
  public String getShortName() {
    return "MessageChainsInspection";
  }

  @NotNull
  public String getGroupDisplayName() {
    return GROUP_NAME;
  }

  private void registerError(ProblemsHolder holder, PsiElement element) {
    holder.registerProblem(element, this.getDisplayName(), quickFix);
  }

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
    return new JavaElementVisitor() {

      @Override
      public void visitExpressionStatement(PsiExpressionStatement statement) {
        super.visitExpressionStatement(statement);

        int count = 0;
        // PsiExpressionStatement内のPsiReferenceExpressionの数を比較
        for (PsiElement element : statement.getChildren()) {
          if (element instanceof PsiReferenceExpression) count++;
        }

        if (count <= numChains) return;

        registerError(holder, statement);
      }
    };
  }
}
