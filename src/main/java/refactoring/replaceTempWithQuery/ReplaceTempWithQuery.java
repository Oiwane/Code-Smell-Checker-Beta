package refactoring.replaceTempWithQuery;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.ide.DataManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.refactoring.tempWithQuery.TempWithQueryHandler;
import visitor.TemporaryVariableVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ui.refactoring.replaceTempWithQuery.SelectTargetTempDialog;

import java.util.List;

public class ReplaceTempWithQuery implements LocalQuickFix {
    public static final String QUICK_FIX_NAME = "Replace Temp with Query";

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return QUICK_FIX_NAME;
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiMethod method = (PsiMethod) descriptor.getPsiElement().getParent();
        TemporaryVariableVisitor temporaryVariableVisitor = new TemporaryVariableVisitor();
        method.accept(temporaryVariableVisitor);

        List<PsiElement> tempVariableList = temporaryVariableVisitor.getTempVariableList();

        SelectTargetTempDialog selectTargetTempDialog = new SelectTargetTempDialog(project, true, tempVariableList);
        ApplicationManager.getApplication().invokeLater(() -> {
            selectTargetTempDialog.show();
            if (!selectTargetTempDialog.isOK()) {
                return;
            }
            final List<Integer> selectedIndexList = selectTargetTempDialog.getSelectedIndexList();

            for (Integer index : selectedIndexList) {
                Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                PsiElement localVariable = tempVariableList.get(index);
                assert editor != null;
                TempWithQueryHandler handler = new TempWithQueryHandler();
                handler.invoke(project, new PsiElement[]{localVariable}, DataManager.getInstance().getDataContext(editor.getComponent()));
            }
        });
    }

}
