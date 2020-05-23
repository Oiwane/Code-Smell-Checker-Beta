package inspection;

import com.intellij.ide.util.PropertiesComponent;
import org.junit.Test;

import static org.junit.Assert.*;

public class InspectionDataTest {
    @Test
    public void getInstance() {
        InspectionData inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.LONG_METHOD);
        assertNotNull(inspectionData);

        inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.LONG_PARAMETER_LIST);
        assertNotNull(inspectionData);

        inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.MESSAGE_CHAINS);
        assertNotNull(inspectionData);
    }

    @Test
    public void getComponentName() {
        InspectionData inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.LONG_METHOD);
        assertEquals("limit value used for LongMethodInspection", inspectionData.getComponentName());

        inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.LONG_PARAMETER_LIST);
        assertEquals("limit value used for LongParameterListInspection", inspectionData.getComponentName());

        inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.MESSAGE_CHAINS);
        assertEquals("limit value used for MessageChainsInspection", inspectionData.getComponentName());
    }

    @Test
    public void getUpperLimitValue() {
        InspectionData inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.LONG_METHOD);
        assertEquals(30, inspectionData.getUpperLimitValue());

        PropertiesComponent component = PropertiesComponent.getInstance();
        component.setValue(inspectionData.getComponentName(), "10");
        assertEquals(10, inspectionData.getUpperLimitValue());
    }
}