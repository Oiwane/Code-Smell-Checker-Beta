package ui.inspectionOption.listener;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import inspection.InspectionData;

import javax.swing.JTextField;

public class OptionButtonListenerTest extends LightJavaCodeInsightFixtureTestCase {

    public void testActionPerformed() {
        JTextField textField = new JTextField("0");
        InspectionData inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.LONG_METHOD);
        String componentName = inspectionData.getComponentName();
        OptionButtonListener listener = new OptionButtonListener(textField, componentName);
        listener.actionPerformed(null);
        String value = PropertiesComponent.getInstance().getValue(componentName);
        assertNull(value);

        textField.setText("3");
        listener.actionPerformed(null);
        value = PropertiesComponent.getInstance().getValue(componentName);
        assertEquals("3", value);
    }
}