package ui.inspectionOption;

import inspection.InspectionData;
import org.junit.Test;
import ui.inspectionOption.listener.OptionButtonListener;

import javax.swing.*;
import java.awt.*;

import static org.junit.Assert.*;

public class InspectionOptionUITest {

    @Test
    public void getTextField() {
        String testDescription = "test";
        int expected = 50;
        InspectionOptionUI optionUI = new InspectionOptionUI(testDescription, expected);
        int actual = Integer.parseInt(optionUI.getTextField().getText());
        assertEquals(expected, actual);
    }

    @Test
    public void createOptionPanel() {
        String testDescription = "test";
        InspectionData inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.LONG_METHOD);
        InspectionOptionUI optionUI = new InspectionOptionUI(testDescription, inspectionData.getUpperLimitValue());
        OptionButtonListener listener = new OptionButtonListener(optionUI.getTextField(), inspectionData.getComponentName());
        JPanel panel = optionUI.createOptionPanel(listener, inspectionData);

        Class[] classes = {
                JPanel.class,   // spinnerPanel
                JLabel.class,   // descriptionLable
                JSpinner.class, // spinner
                JPanel.class,   // buttonPanel
                JButton.class}; // button
        int index = 0;
        for (Component child : panel.getComponents()) {
            assertEquals(classes[index++], child.getClass());
            for (Component grandChild : ((JComponent) child).getComponents()) {
                if (grandChild instanceof JLabel) {
                    JLabel label = (JLabel) grandChild;
                    assertEquals(testDescription, label.getText());
                }
                assertEquals(classes[index++], grandChild.getClass());
            }
        }
    }
}