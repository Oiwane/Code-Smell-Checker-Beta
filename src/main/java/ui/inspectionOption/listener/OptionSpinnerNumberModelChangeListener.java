package ui.inspectionOption.listener;

import inspection.InspectionData;
import org.jetbrains.annotations.NotNull;
import ui.inspectionOption.InspectionOptionUtil;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class OptionSpinnerNumberModelChangeListener implements ChangeListener {
    private JButton button;
    private JTextField textField;
    private InspectionData inspectionData;

    public OptionSpinnerNumberModelChangeListener(@NotNull JButton button_, @NotNull JTextField textField_, @NotNull InspectionData data) {
        button = button_;
        textField = textField_;
        inspectionData = data;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        InspectionOptionUtil.changeAvailabilityButton(button, textField, inspectionData);
    }
}
