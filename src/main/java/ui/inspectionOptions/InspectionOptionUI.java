package ui.inspectionOptions;

import javax.swing.*;
import java.awt.event.ActionListener;

import static ui.inspectionOptions.InspectionOptionsUtil.LIMIT_MIN_VALUE;
import static ui.inspectionOptions.InspectionOptionsUtil.disableInvalidInput;

public class InspectionOptionUI {

  private JPanel panel;
  private JPanel subPanel1;
  private JPanel subPanel2;
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
    subPanel1 = new JPanel();
    subPanel2 = new JPanel();
    descriptionLabel = new JLabel(description);
    spinnerNumberModel = new SpinnerNumberModel(initialValue, LIMIT_MIN_VALUE, null, 1);
    spinner = new JSpinner(spinnerNumberModel);
    button = new JButton("set value");
  }

  public SpinnerNumberModel getSpinnerNumberModel() { return spinnerNumberModel; }

  public JPanel createOptionPanel(ActionListener listener) {
    subPanel1.setLayout(new BoxLayout(subPanel1, BoxLayout.X_AXIS));
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    button.addActionListener(listener);
    disableInvalidInput(spinner);

    subPanel1.add(descriptionLabel);
    subPanel1.add(spinner);

    subPanel2.add(button);

    panel.add(subPanel1);
    panel.add(subPanel2);

    return panel;
  }
}
