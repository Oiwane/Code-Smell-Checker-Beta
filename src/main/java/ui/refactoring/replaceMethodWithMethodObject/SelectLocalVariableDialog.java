package ui.refactoring.replaceMethodWithMethodObject;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import ui.refactoring.SelectReplacedElementsDialog;

import java.util.List;

public class SelectLocalVariableDialog extends SelectReplacedElementsDialog {
  private static final String ERROR_TEXT = "No exist target local variables.";

  public SelectLocalVariableDialog(@Nullable Project project, boolean canBeParent, List<PsiElement> replacedElementList) {
    super(project, canBeParent, replacedElementList, ERROR_TEXT);
    setTitle("Select Local Variables");

    init();
  }
}
