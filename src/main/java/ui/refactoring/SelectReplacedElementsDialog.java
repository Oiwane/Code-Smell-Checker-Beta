package ui.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

public class SelectReplacedElementsDialog extends DialogWrapper {
  private JPanel mainPanel;
  private JPanel checkboxPanel;
  private final List<PsiElement> replacedElementList;
  private List<Integer> selectedIndexList;
  private final Box verticalBox;
  private String errorText;

  protected SelectReplacedElementsDialog(@Nullable Project project, boolean canBeParent, List<PsiElement> replacedElementList, @NotNull String errorText) {
    super(project, canBeParent);
    super.init();

    this.replacedElementList = replacedElementList;
    this.errorText = errorText;
    selectedIndexList = new ArrayList<>();
    verticalBox = Box.createVerticalBox();
    setUpCheckBoxesPanel();
  }

  private void setUpCheckBoxesPanel() {
    if (replacedElementList.size() == 0) {
      checkboxPanel.add(new JLabel(errorText));
    } else {
      setUpCheckBoxes();
    }
  }

  private void setUpCheckBoxes() {
    for (PsiElement element : replacedElementList) {
      final JCheckBox checkBox = new JCheckBox(element.getText());
      checkBox.setSelected(true);
      verticalBox.add(checkBox);
    }
    checkboxPanel.add(verticalBox);
  }

  @Override
  protected void doOKAction() {
    super.doOKAction();

    if (replacedElementList.size() == 0) return;

    int index = 0;
    for (Component component : verticalBox.getComponents()) {
      if (!(component instanceof JCheckBox)) continue;
      JCheckBox checkBox = (JCheckBox) component;
      if (checkBox.isSelected()) selectedIndexList.add(index);
      index++;
    }
  }

  public List<Integer> getSelectedIndexList() {
    return selectedIndexList;
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    return mainPanel;
  }
}
