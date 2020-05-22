package ui.inspectionOption.listener;

import inspection.InspectionData;
import org.jetbrains.annotations.NotNull;
import ui.inspectionOption.InspectionOptionUtil;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class OptionTextFieldDocumentListener implements DocumentListener {
    private JButton button;
    private JTextField textField;
    private InspectionData inspectionData;

    public OptionTextFieldDocumentListener(@NotNull JButton button_, @NotNull JTextField textField_, @NotNull InspectionData data) {
        button = button_;
        textField = textField_;
        inspectionData = data;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        InspectionOptionUtil.changeAvailabilityButton(button, textField, inspectionData);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        InspectionOptionUtil.changeAvailabilityButton(button, textField, inspectionData);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }
}
