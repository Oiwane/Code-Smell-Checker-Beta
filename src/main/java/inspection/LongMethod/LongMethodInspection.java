package inspection.LongMethod;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import inspection.Setting;
import org.jetbrains.annotations.NotNull;

public class LongMethodInspection extends AbstractBaseJavaLocalInspectionTool {
  private LocalQuickFix quickFix = new LongMethodFix();
  private int numLines;

  public LongMethodInspection() {
    numLines = Setting.numLines;
    System.out.println("LongMethodInspection start");
  }

  @Override
  @NotNull
  public String getDisplayName() {
      return "This method is long";
  }

  private void registerError(ProblemsHolder holder, PsiElement element) {
    holder.registerProblem(element, this.getDisplayName(), quickFix);
  }

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
    return new JavaElementVisitor() {

      @Override
      public void visitMethod(PsiMethod method) {
        super.visitMethod(method);

        if (method.getBody() == null || method.getBody().getStatementCount() < numLines) {
          return;
        }

        registerError(holder, method);
      }
    };
  }
}
