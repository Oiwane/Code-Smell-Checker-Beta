package ui.refactoring.replaceTempWithQuery;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import ui.refactoring.SelectReplacedElementsDialog;

import java.util.List;

public class SelectTargetTempDialog extends SelectReplacedElementsDialog {

    private static final String ERROR_TEXT = "No exist temporary variables.";

    public SelectTargetTempDialog(@Nullable Project project, boolean canBeParent, List<PsiElement> tempVariableList) {
        super(project, canBeParent, tempVariableList, ERROR_TEXT);
        setTitle("Select Temps");
    }
}
