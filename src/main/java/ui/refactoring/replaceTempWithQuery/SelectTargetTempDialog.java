package ui.refactoring.replaceTempWithQuery;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import ui.refactoring.SelectReplacedElementsDialog;

import java.util.List;

public class SelectTargetTempDialog extends SelectReplacedElementsDialog {

    private static final String ERROR_TEXT = "No exist temporary variables.";
    private final boolean isOKReturnValue;
    private final boolean isTest;

    public SelectTargetTempDialog(@Nullable Project project, boolean canBeParent, List<PsiElement> tempVariableList, boolean isTest, boolean isOKReturnValue) {
        super(project, canBeParent, tempVariableList, ERROR_TEXT);
        setTitle("Select Temps");
        this.isOKReturnValue = isOKReturnValue;
        this.isTest = isTest;
    }

    public SelectTargetTempDialog(@Nullable Project project, boolean canBeParent, List<PsiElement> tempVariableList, boolean isTest) {
        this(project, canBeParent, tempVariableList, isTest, false);
    }

    @Override
    public void show() {
        if (!isTest)
            super.show();
    }

    @Override
    public boolean isOK() {
        if (isTest)
            return isOKReturnValue;
        return super.isOK();
    }
}
