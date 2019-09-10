package ui.inspectionOptions;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;

public class InspectionOptionsUtil {
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
}
