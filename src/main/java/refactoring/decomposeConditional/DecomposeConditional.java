package refactoring.decomposeConditional;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.refactoring.extractMethod.ExtractMethodHandler;
import com.intellij.refactoring.extractMethod.ExtractMethodProcessor;
import visitor.ConditionalVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ui.refactoring.decomposeConditional.SelectTargetConditionalDialog;

import java.util.List;

public class DecomposeConditional implements LocalQuickFix {
    public static final String QUICK_FIX_NAME = "Decompose Conditional";

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return QUICK_FIX_NAME;
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        ApplicationManager.getApplication().invokeLater(() -> {
            PsiMethod method = (PsiMethod) descriptor.getPsiElement().getParent();

            ConditionalVisitor conditionalVisitor = new ConditionalVisitor();
            method.accept(conditionalVisitor);

            final List<PsiElement> conditionalList = conditionalVisitor.getConditionalList();

            SelectTargetConditionalDialog selectTargetConditionalDialog = new SelectTargetConditionalDialog(project, true, conditionalList);

            selectTargetConditionalDialog.show();
            if (selectTargetConditionalDialog.isOK()) {
                final List<Integer> selectedIndexList = selectTargetConditionalDialog.getSelectedIndexList();

                for (Integer index : selectedIndexList) {
                    PsiElement[] elements = new PsiElement[]{conditionalList.get(index)};
                    ExtractMethodProcessor processor = ExtractMethodHandler.getProcessor(project, elements, method.getContainingFile(), false);
                    assert processor != null;

                    ExtractMethodHandler.invokeOnElements(project, processor, method.getContainingFile(), true);
                }
            }
        });
    }
}
