package ui.inspectionOptions;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.ui.messages.MessageDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static ui.inspectionOptions.InspectionOptionsUtil.LIMIT_MIN_VALUE;

/**
 * UI上のボタンを押したかを監視するリスナークラス
 */
public class InspectionOptionListener implements ActionListener {
  private final SpinnerNumberModel myModel;
  private final String mySuccessMessage;
  private final String myErrorMessage;
  private final String myPropertiesComponentName;

  public InspectionOptionListener(SpinnerNumberModel model, String successMessage,
                                  String errorMessage, String propertiesComponentName) {
    myModel = model;
    mySuccessMessage = successMessage;
    myErrorMessage = errorMessage;
    myPropertiesComponentName = propertiesComponentName;
  }

  public void actionPerformed(ActionEvent e) {
    Integer value = (Integer) myModel.getValue();
    MessageDialog messageDialog;

    if (value < LIMIT_MIN_VALUE) {
      String title = "Error : Invalid value";
      messageDialog = new MessageDialog(myErrorMessage, title, new String[]{"OK"}, 1, null);
    } else {
      String title = "Dialog : Success";
      PropertiesComponent.getInstance().setValue(myPropertiesComponentName, String.valueOf(value));
      messageDialog = new MessageDialog(mySuccessMessage, title, new String[]{"OK"}, 1, null);
    }
    messageDialog.show();
  }

}
