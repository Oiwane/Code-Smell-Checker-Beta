package ui.inspectionOptions;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.ui.messages.MessageDialog;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;

public class InspectionOptionsUtil {
  final static int LIMIT_MIN_VALUE = 1;

  public final static String LONG_METHOD_PROPERTIES_COMPONENT_NAME = "limit value used for LongMethodInspection";
  public final static String LONG_PARAMETER_LIST_PROPERTIES_COMPONENT_NAME = "limit value used for LongParameterListInspection";
  public final static String MESSAGE_CHAINS_PROPERTIES_COMPONENT_NAME = "limit value used for MessageChainsInspection";

  public final static String TOO_SMALL_VALUE = "The value you set is too small.\nChange to another value.";

  /**
   * スピナーに無効な値を入力できないようにする
   *
   * @param spinner 対象のスピナー
   */
  static void disableInvalidInput(@NotNull JSpinner spinner) {
    JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
    DefaultFormatter formatter = (DefaultFormatter) editor.getTextField().getFormatter();
    formatter.setAllowsInvalid(false);
  }

  public static void setUpValueUsedInspection(int value, String propertiesComponentName,
                                                 String successMessage, String errorMessage) {
    MessageDialog messageDialog;

    if (value < LIMIT_MIN_VALUE) {
      String title = "Error : Invalid value";
      messageDialog = new MessageDialog(errorMessage, title, new String[]{"OK"}, 1, null);
    } else {
      String title = "Dialog : Success";
      PropertiesComponent.getInstance().setValue(propertiesComponentName, String.valueOf(value));
      messageDialog = new MessageDialog(successMessage, title, new String[]{"OK"}, 1, null);
    }
    messageDialog.show();
  }
}
