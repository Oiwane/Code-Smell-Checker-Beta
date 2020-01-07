package inspection;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import org.jetbrains.annotations.NotNull;
import ui.inspectionOption.InspectionOptionUI;
import ui.inspectionOption.listener.OptionButtonListener;

import javax.swing.*;

import static ui.inspectionOption.InspectionOptionUtil.TOO_SMALL_VALUE;

public abstract class CodeSmellInspection extends AbstractBaseJavaLocalInspectionTool {

  protected JComponent createOptionUI(String description, @NotNull InspectionData data) {
    InspectionOptionUI optionUI = new InspectionOptionUI(description, InspectionUtil.getUpperLimitValue(data));
    OptionButtonListener listener = new OptionButtonListener(optionUI.getTextField(), data.getComponentName());

    return optionUI.createOptionPanel(listener, data);
  }

  @NotNull
  public String getGroupDisplayName() {
    return InspectionUtil.GROUP_NAME;
  }

  public boolean isEnabledByDefault() {
    return InspectionUtil.IS_ENABLED_BY_DEFAULT;
  }

  @NotNull
  public HighlightDisplayLevel getDefaultLevel() {
    return HighlightDisplayLevel.WARNING;
  }

  public String getWorked() {
    return null;
  }
}
