package ui.inspectionOption.listener;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import inspection.InspectionData;

import javax.swing.JButton;
import javax.swing.JTextField;

public class OptionTextFieldDocumentListenerTest extends LightJavaCodeInsightFixtureTestCase {

    public void testInsertUpdate() {
        JButton button = new JButton();
        JTextField textField = new JTextField("1");
        InspectionData inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.LONG_METHOD);
        OptionTextFieldDocumentListener listener = new OptionTextFieldDocumentListener(button, textField, inspectionData);

        listener.insertUpdate(null);
    }

    public void testRemoveUpdate() {
        JButton button = new JButton();
        JTextField textField = new JTextField("1");
        InspectionData inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.LONG_METHOD);
        OptionTextFieldDocumentListener listener = new OptionTextFieldDocumentListener(button, textField, inspectionData);

        listener.removeUpdate(null);
    }

    public void testChangedUpdate() {
        JButton button = new JButton();
        JTextField textField = new JTextField("1");
        InspectionData inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.LONG_METHOD);
        OptionTextFieldDocumentListener listener = new OptionTextFieldDocumentListener(button, textField, inspectionData);

        listener.changedUpdate(null);
    }
}