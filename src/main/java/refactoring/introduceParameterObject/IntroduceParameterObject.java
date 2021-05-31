package refactoring.introduceParameterObject;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ui.refactoring.introduceParameterObject.MyIntroduceParameterObjectDialog;

public class IntroduceParameterObject implements LocalQuickFix {
    private static final String QUICK_FIX_NAME = "Introduce Parameter Object";
    private final boolean isTest;

    public IntroduceParameterObject() {
        this(false);
    }

    public IntroduceParameterObject(boolean isTest) {
        this.isTest = isTest;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return QUICK_FIX_NAME;
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiParameterList parameterList = (PsiParameterList) descriptor.getPsiElement();
        PsiMethod method = (PsiMethod) parameterList.getParent();

        ApplicationManager.getApplication().invokeLater(() -> {
            TransactionGuard.getInstance().submitTransactionAndWait(() -> {
                MyIntroduceParameterObjectDialog dialog = new MyIntroduceParameterObjectDialog(method, isTest);
                dialog.show();
            });
        });
    }
}
