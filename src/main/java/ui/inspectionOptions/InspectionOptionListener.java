package ui.inspectionOptions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    InspectionOptionsUtil.setUpValueUsedInspection(value, myPropertiesComponentName, mySuccessMessage, myErrorMessage);
  }

}
