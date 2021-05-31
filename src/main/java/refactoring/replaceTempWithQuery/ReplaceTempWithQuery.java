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
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ui.refactoring.replaceTempWithQuery.MyTempWithQueryHandler;
import ui.refactoring.replaceTempWithQuery.SelectTargetTempDialog;
import visitor.TemporaryVariableVisitor;

import java.util.List;

public class ReplaceTempWithQuery implements LocalQuickFix {
    private static final String QUICK_FIX_NAME = "Replace Temp with Query";
    private final boolean isOKReturnValue; // テスト時のSelectTargetTempDialog.isOK()の戻り値
    private final boolean isTest;

    public ReplaceTempWithQuery() {
        this(false, false);
    }

    public ReplaceTempWithQuery(boolean isTest, boolean isOKReturnValue) {
        this.isOKReturnValue = isOKReturnValue;
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
        PsiMethod method = (PsiMethod) descriptor.getPsiElement().getParent();
        TemporaryVariableVisitor temporaryVariableVisitor = new TemporaryVariableVisitor();
        method.accept(temporaryVariableVisitor);

        List<PsiElement> tempVariableList = temporaryVariableVisitor.getTempVariableList();

        SelectTargetTempDialog dialog = new SelectTargetTempDialog(project, true, tempVariableList, isTest, isOKReturnValue);
        ApplicationManager.getApplication().invokeLater(() -> {
            dialog.show();
            if (!dialog.isOK()) {
                return;
            }
            final List<Integer> selectedIndexList = dialog.getSelectedIndexList();

            for (Integer index : selectedIndexList) {
                Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                PsiElement localVariable = tempVariableList.get(index);
                assert editor != null;
                MyTempWithQueryHandler handler = new MyTempWithQueryHandler(isTest);
                handler.invoke(project, new PsiElement[]{localVariable}, DataManager.getInstance().getDataContext(editor.getComponent()));
            }
        });
    }

}
