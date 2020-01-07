package ui.inspectionOption.listener;

import com.intellij.ide.util.PropertiesComponent;
import ui.inspectionOption.InspectionOptionUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * UI上のボタンを押したかを監視するリスナークラス
 */
public class OptionButtonListener implements ActionListener {
  private JTextField myTextField;
  private final String myPropertiesComponentName;

  public OptionButtonListener(JTextField textField, String propertiesComponentName) {
    myTextField = textField;
    myPropertiesComponentName = propertiesComponentName;
  }

  public void actionPerformed(ActionEvent e) {
    String value = myTextField.getText();

    if (Integer.parseInt(value) >= InspectionOptionUtil.LIMIT_MIN_VALUE) {
      PropertiesComponent.getInstance().setValue(myPropertiesComponentName, value);
    }
  }

}
