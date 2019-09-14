package ui.inspectionOptions;

import javax.swing.*;
import java.awt.event.ActionListener;

import static ui.inspectionOptions.InspectionOptionsUtil.LIMIT_MIN_VALUE;
import static ui.inspectionOptions.InspectionOptionsUtil.disableInvalidInput;

public class InspectionOptionUI {

  private JPanel panel;
  private JPanel spinnerPanel;
  private JPanel buttonPanel;
  private JLabel descriptionLabel;
  private SpinnerNumberModel spinnerNumberModel;
  private JSpinner spinner;
  private JButton button;

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
    button = new JButton("set value");
  }

  public SpinnerNumberModel getSpinnerNumberModel() { return spinnerNumberModel; }

  public JPanel createOptionPanel(ActionListener listener) {
    spinnerPanel.setLayout(new BoxLayout(spinnerPanel, BoxLayout.X_AXIS));
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    button.addActionListener(listener);
    disableInvalidInput(spinner);

    spinnerPanel.add(descriptionLabel);
    spinnerPanel.add(spinner);

    buttonPanel.add(button);

    panel.add(spinnerPanel);
    panel.add(buttonPanel);

    return panel;
  }
}
