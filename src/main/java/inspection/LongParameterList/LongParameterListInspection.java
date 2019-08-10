package inspection.LongParameterList;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReturnStatement;
import org.jetbrains.annotations.NotNull;

public class LongParameterListInspection extends AbstractBaseJavaLocalInspectionTool {
    private final LocalQuickFix quickFix = new LongParameterListFix();

    @Override
    @NotNull
    public String getDisplayName() {
        return "Long parameter list";
    }

    private void registerError(ProblemsHolder holder, PsiElement element) {
        holder.registerProblem(element, getDisplayName(), quickFix);
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
