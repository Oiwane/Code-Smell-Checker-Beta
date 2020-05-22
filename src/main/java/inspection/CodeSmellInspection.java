package inspection;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import org.jetbrains.annotations.NotNull;
import ui.inspectionOption.InspectionOptionUI;
import ui.inspectionOption.listener.OptionButtonListener;

import javax.swing.JComponent;

public abstract class CodeSmellInspection extends AbstractBaseJavaLocalInspectionTool {
    protected InspectionData inspectionData;
    protected int upperLimitValue;
    protected String displayName;

    protected JComponent createOptionUI(String description, @NotNull InspectionData data) {
        InspectionOptionUI optionUI = new InspectionOptionUI(description, data.getUpperLimitValue());
        OptionButtonListener listener = new OptionButtonListener(optionUI.getTextField(), data.getComponentName());

        return optionUI.createOptionPanel(listener, data);
    }

    @Override
    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    @NotNull
    public String getGroupDisplayName() {
        return "Code smell";
    }

    public boolean isEnabledByDefault() {
        return false;
    }

    @NotNull
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }
}
