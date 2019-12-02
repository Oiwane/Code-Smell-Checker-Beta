package ui.inspectionOptions;

import ui.inspectionOptions.listener.OptionSpinnerNumberModelChangeListener;
import ui.inspectionOptions.listener.OptionTextFieldDocumentListener;

import javax.swing.*;
import java.awt.event.*;

import static ui.inspectionOptions.InspectionOptionsUtil.LIMIT_MIN_VALUE;
import static ui.inspectionOptions.InspectionOptionsUtil.disableInvalidInput;

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
   * @param description 設定する値の説明文
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

  public JTextField getTextField() {
    return textField;
  }

  public JPanel createOptionPanel(ActionListener listener, String propertiesComponentName) {
    spinnerPanel.setLayout(new BoxLayout(spinnerPanel, BoxLayout.X_AXIS));
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    button.addActionListener(listener);
    button.setEnabled(false);
    disableInvalidInput(spinner);

    // リスナーの登録
    spinnerNumberModel.addChangeListener(new OptionSpinnerNumberModelChangeListener(button, textField, propertiesComponentName));
    textField.getDocument().addDocumentListener(new OptionTextFieldDocumentListener(button, textField, propertiesComponentName));
    textField.addActionListener(e -> {
      if (button.isEnabled()) {
        button.doClick();
        button.setEnabled(false);
      }
    });

    spinnerPanel.add(descriptionLabel);
    spinnerPanel.add(spinner);

    buttonPanel.add(button);

    panel.add(spinnerPanel);
    panel.add(buttonPanel);

    return panel;
  }
}
