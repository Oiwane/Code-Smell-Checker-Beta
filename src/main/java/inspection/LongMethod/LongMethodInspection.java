package inspection.LongMethod;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReturnStatement;
import org.jetbrains.annotations.NotNull;

public class LongMethodInspection extends AbstractBaseJavaLocalInspectionTool {
    private LocalQuickFix quickFix = new LongMethodFix();

    @Override
    @NotNull
    public String getDisplayName() {
        return "Long method";
    }

    private void registerError(ProblemsHolder holder, PsiElement element) {
        holder.registerProblem(element, this.getDisplayName(), quickFix);
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitReturnStatement(PsiReturnStatement statement) {
                super.visitReturnStatement(statement);
            }
        };
    }
}
