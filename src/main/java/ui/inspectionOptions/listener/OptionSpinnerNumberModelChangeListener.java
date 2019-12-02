package ui.inspectionOptions.listener;

import org.jetbrains.annotations.NotNull;
import ui.inspectionOptions.InspectionOptionsUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class OptionSpinnerNumberModelChangeListener implements ChangeListener {
  private JButton button;
  private JTextField textField;
  private String propertiesComponentName;

  public OptionSpinnerNumberModelChangeListener(@NotNull JButton button_, @NotNull JTextField textField_, @NotNull String propertiesComponentName_) {
    button = button_;
    textField = textField_;
    propertiesComponentName = propertiesComponentName_;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    InspectionOptionsUtil.changeAvailabilityButton(button, textField, propertiesComponentName);
  }
}
