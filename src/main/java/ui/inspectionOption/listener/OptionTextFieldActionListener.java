package ui.inspectionOption.listener;

import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OptionTextFieldActionListener implements ActionListener {
    private final JButton button;

    public OptionTextFieldActionListener(JButton button) {
        this.button = button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (button.isEnabled()) {
            button.doClick();
            button.setEnabled(false);
        }
    }
}
