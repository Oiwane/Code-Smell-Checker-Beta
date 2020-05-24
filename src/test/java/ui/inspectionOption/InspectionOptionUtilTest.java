package ui.inspectionOption;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import inspection.InspectionData;
import org.junit.Test;

import javax.swing.*;

import static org.junit.Assert.*;

public class InspectionOptionUtilTest extends LightJavaCodeInsightFixtureTestCase {

    public void testChangeAvailabilityButton() {
        JButton button = new JButton();
        JTextField textField = new JTextField("1");
        InspectionData inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.LONG_METHOD);

        InspectionOptionUtil.changeAvailabilityButton(button, textField, inspectionData);
        assertTrue(button.isEnabled());
        textField.setText(String.valueOf(inspectionData.getUpperLimitValue()));
        InspectionOptionUtil.changeAvailabilityButton(button, textField, inspectionData);
    }
}