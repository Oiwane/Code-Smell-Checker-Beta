package ui.refactoring.extractMethod;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SelectExtractedRangeDialog extends DialogWrapper {
  private JPanel mainPanel;
  private JPanel textFieldPanel;
  private PsiMethod method;

  public SelectExtractedRangeDialog(@Nullable Project project, boolean canBeParent, PsiMethod method) {
    super(project, canBeParent);
    this.method = method;

    super.init();
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    return mainPanel;
  }

  private void createUIComponents() {
    textFieldPanel = new MyEditorTextField(method.getText(), method.getProject(), method.getContainingFile().getFileType());
  }

  public Editor getEditor() {
    return ((MyEditorTextField) textFieldPanel).getEditor();
  }
}
