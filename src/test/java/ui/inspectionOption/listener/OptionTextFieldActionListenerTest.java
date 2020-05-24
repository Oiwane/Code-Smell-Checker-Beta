package ui.inspectionOption.listener;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

import javax.swing.*;

public class OptionTextFieldActionListenerTest extends LightJavaCodeInsightFixtureTestCase {

    public void testActionPerformed() {
        JButton button = new JButton();
        OptionTextFieldActionListener listener = new OptionTextFieldActionListener(button);

        button.setEnabled(true);
        listener.actionPerformed(null);
        assertFalse(button.isEnabled());

        button.setEnabled(false);
        listener.actionPerformed(null);
        assertFalse(button.isEnabled());
    }
}