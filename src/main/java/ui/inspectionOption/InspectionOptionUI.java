package ui.inspectionOption;

import inspection.InspectionData;
import org.jetbrains.annotations.NotNull;
import ui.inspectionOption.listener.OptionButtonListener;
import ui.inspectionOption.listener.OptionSpinnerNumberModelChangeListener;
import ui.inspectionOption.listener.OptionTextFieldDocumentListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DefaultFormatter;
import java.awt.event.ActionListener;

import static ui.inspectionOption.InspectionOptionUtil.LIMIT_MIN_VALUE;

/**
 * 各インスペクションの検出条件を設定する画面のUIを生成するクラス
 */
public class InspectionOptionUI {
    private final JPanel panel;
    private final JPanel spinnerPanel;
    private final JPanel buttonPanel;
    private final JLabel descriptionLabel;
    private final SpinnerNumberModel spinnerNumberModel;
    private final JSpinner spinner;
    private final JTextField textField;
    private final JButton button;

    /**
     * Inspectionの設定画面のオプション
     *
     * @param description  設定する値の説明文
     * @param initialValue spinnerの初期値
     */
    public InspectionOptionUI(String description, int initialValue) {
        panel = new JPanel();
        spinnerPanel = new JPanel();
        buttonPanel = new JPanel();
        descriptionLabel = new JLabel(description);
        spinnerNumberModel = new SpinnerNumberModel(initialValue, LIMIT_MIN_VALUE, null, 1);
        spinner = new JSpinner(spinnerNumberModel);
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        textField = editor.getTextField();
        button = new JButton("set value");
    }

    /**
     * スピナーに無効な値を入力できないようにする
     *
     * @param spinner 対象のスピナー
     */
    private void disableInvalidInput(@NotNull JSpinner spinner) {
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        DefaultFormatter formatter = (DefaultFormatter) editor.getTextField().getFormatter();
        formatter.setAllowsInvalid(false);
    }

    public JTextField getTextField() {
        return textField;
    }

    public JPanel createOptionPanel(OptionButtonListener listener, InspectionData inspectionData) {
        spinnerPanel.setLayout(new BoxLayout(spinnerPanel, BoxLayout.X_AXIS));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        button.addActionListener(listener);
        button.setEnabled(false);
        this.disableInvalidInput(spinner);

        // リスナーの登録
        spinnerNumberModel.addChangeListener(new OptionSpinnerNumberModelChangeListener(button, textField, inspectionData));
        textField.getDocument().addDocumentListener(new OptionTextFieldDocumentListener(button, textField, inspectionData));
        ActionListener actionListener = e -> {
            if (button.isEnabled()) {
                button.doClick();
                button.setEnabled(false);
            }
        };
        textField.addActionListener(actionListener);

        spinnerPanel.add(descriptionLabel);
        spinnerPanel.add(spinner);

        buttonPanel.add(button);

        panel.add(spinnerPanel);
        panel.add(buttonPanel);

        return panel;
    }
}
