package ui.inspectionOption;

import inspection.InspectionData;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JTextField;

/**
 * オプションUIの処理に関するクラス
 */
public class InspectionOptionUtil {
    public final static int LIMIT_MIN_VALUE = 1;

    private InspectionOptionUtil() {
    }

    /**
     * オプション画面のボタンの使用可否を変更する
     *
     * @param button    対象のボタン
     * @param textField スピナーのテキストフィールド
     * @param data      インスペクションの設定値
     */
    public static void changeAvailabilityButton(@NotNull JButton button, @NotNull JTextField textField, @NotNull InspectionData data) {
        int value = data.getUpperLimitValue();
        button.setEnabled(!textField.getText().equals(String.valueOf(value)));
    }
}
