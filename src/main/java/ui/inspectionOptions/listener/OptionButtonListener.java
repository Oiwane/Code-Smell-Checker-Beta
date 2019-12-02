package ui.inspectionOptions.listener;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.ui.messages.MessageDialog;
import ui.inspectionOptions.InspectionOptionsUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * UI上のボタンを押したかを監視するリスナークラス
 */
public class OptionButtonListener implements ActionListener {
  private JTextField myTextField;
  private final String mySuccessMessage;
  private final String myErrorMessage;
  private final String myPropertiesComponentName;

  public OptionButtonListener(JTextField textField, String successMessage,
                              String errorMessage, String propertiesComponentName) {
    myTextField = textField;
    mySuccessMessage = successMessage;
    myErrorMessage = errorMessage;
    myPropertiesComponentName = propertiesComponentName;
  }

  public void actionPerformed(ActionEvent e) {
    MessageDialog messageDialog;

    String value = myTextField.getText();

    if (Integer.parseInt(value) < InspectionOptionsUtil.LIMIT_MIN_VALUE) {
      String title = "Error : Invalid value";
      messageDialog = new MessageDialog(myErrorMessage, title, new String[]{"OK"}, 1, null);
    } else {
      String title = "Dialog : Success";
      PropertiesComponent.getInstance().setValue(myPropertiesComponentName, value);
      messageDialog = new MessageDialog(mySuccessMessage, title, new String[]{"OK"}, 1, null);
    }
    messageDialog.show();
  }

}
