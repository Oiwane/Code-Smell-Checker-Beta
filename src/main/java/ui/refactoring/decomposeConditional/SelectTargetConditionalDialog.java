package ui.refactoring.decomposeConditional;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import ui.refactoring.SelectReplacedElementsDialog;

import java.util.List;

public class SelectTargetConditionalDialog extends SelectReplacedElementsDialog {
  private static final String ERROR_TEXT = "No exist target conditional expression.";

  public SelectTargetConditionalDialog(@Nullable Project project, boolean canBeParent, List<PsiElement> replacedElementList) {
    super(project, canBeParent, replacedElementList, ERROR_TEXT);
    setTitle("Select Conditionals");
  }
}
