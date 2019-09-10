package ui.inspectionOptions;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.ui.messages.MessageDialog;
import inspection.LongMethod.LongMethodInspection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * オプション画面の設定をする
 */
public class LongMethodInspectionOption {
  private int minimum;
  private JPanel panel;
  private JPanel subPanel1;
  private JPanel subPanel2;
  private JLabel explainLabel;
  private SpinnerNumberModel spinnerNumberModel;
  private JSpinner spinner;
  private JButton button;

  public LongMethodInspectionOption() {
    minimum = 1;
    panel = new JPanel();
    subPanel1 = new JPanel();
    subPanel2 = new JPanel();
    explainLabel = new JLabel("detected method length : ");
    spinnerNumberModel = new SpinnerNumberModel(LongMethodInspection.initNumOfLine(), minimum, null, 1);
    spinner = new JSpinner(spinnerNumberModel);
    button = new JButton("set value");
  }

  /**
   * オプションの設定をする画面を作成する
   *
   * @return オプション画面のパネル
   */
  public JPanel createOptionPanel() {
    subPanel1.setLayout(new BoxLayout(subPanel1, BoxLayout.X_AXIS));
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    button.addActionListener(new LongMethodInspectionActionListener());
    InspectionOptionsUtil.disableInvalidInput(spinner);

    subPanel1.add(explainLabel);
    subPanel1.add(spinner);

    subPanel2.add(button);

    panel.add(subPanel1);
    panel.add(subPanel2);

    return panel;
  }

  /**
   * オプション画面のボタンの押下を監視する
   */
  private class LongMethodInspectionActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      Integer value = (Integer) spinnerNumberModel.getValue();
      MessageDialog messageDialog;

      if (value < minimum) {
        String message = "The value you set is too small.\n" +
                "Change to another value.";
        String title = "Error : Invalid value";
        messageDialog = new MessageDialog(message, title, new String[]{"OK"}, 1, null);
      } else {
        String message = "save detected method length";
        String title = "Dialog : Success";
        PropertiesComponent.getInstance().setValue("value of LongMethodInspection", String.valueOf(value));
        messageDialog = new MessageDialog(message, title, new String[]{"OK"}, 1, null);
      }
      messageDialog.show();
    }
  }
}
