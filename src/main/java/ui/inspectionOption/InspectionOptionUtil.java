package ui.inspectionOption;

import com.intellij.ide.util.PropertiesComponent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * オプションUIの処理に関するクラス
 */
public class InspectionOptionUtil {
  public final static int LIMIT_MIN_VALUE = 1;

  public final static String TOO_SMALL_VALUE = "The value you set is too small.\nChange to another value.";

  /**
   * オプション画面のボタンの使用可否を変更する
   *
   * @param button 対象のボタン
   * @param textField スピナーのテキストフィールド
   * @param propertiesComponentName 値の保存先の名前
   */
  public static void changeAvailabilityButton(@NotNull JButton button, @NotNull JTextField textField, @NotNull String propertiesComponentName) {
    String value = PropertiesComponent.getInstance().getValue(propertiesComponentName);
    if (value == null) {
      button.setEnabled(false);
      return;
    }
    button.setEnabled(!textField.getText().equals(value));
  }
}
