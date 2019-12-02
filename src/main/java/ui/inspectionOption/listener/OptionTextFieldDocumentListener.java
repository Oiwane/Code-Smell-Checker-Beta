package ui.inspectionOption.listener;

import org.jetbrains.annotations.NotNull;
import ui.inspectionOption.InspectionOptionUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class OptionTextFieldDocumentListener implements DocumentListener {
  private JButton button;
  private JTextField textField;
  private String propertiesComponentName;

  public OptionTextFieldDocumentListener(@NotNull JButton button_, @NotNull JTextField textField_, @NotNull String propertiesComponentName_) {
    button = button_;
    textField = textField_;
    propertiesComponentName = propertiesComponentName_;
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    InspectionOptionUtil.changeAvailabilityButton(button, textField, propertiesComponentName);
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    InspectionOptionUtil.changeAvailabilityButton(button, textField, propertiesComponentName);
  }

  @Override
  public void changedUpdate(DocumentEvent e) {
  }
}
