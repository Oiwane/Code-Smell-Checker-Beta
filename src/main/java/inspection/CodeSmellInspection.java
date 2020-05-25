package inspection;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ui.inspectionOption.InspectionOptionUI;
import ui.inspectionOption.listener.OptionButtonListener;

import javax.swing.JComponent;

public abstract class CodeSmellInspection extends AbstractBaseJavaLocalInspectionTool {
    private InspectionData inspectionData;

    protected JComponent createOptionUI(String description, @NotNull InspectionData data) {
        InspectionOptionUI optionUI = new InspectionOptionUI(description, data.getUpperLimitValue());
        OptionButtonListener listener = new OptionButtonListener(optionUI.getTextField(), data.getComponentName());

        return optionUI.createOptionPanel(listener, data);
    }

    protected int getUpperLimitValue() {
        return inspectionData.getUpperLimitValue();
    }

    public void setInspectionData(InspectionData inspectionData) {
        this.inspectionData = inspectionData;
    }

    public InspectionData getInspectionData() {
        return inspectionData;
    }

    @Override
    @NotNull
    public abstract String getDisplayName();

    @NotNull
    @Contract(pure = true)
    public final String getGroupDisplayName() {
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
