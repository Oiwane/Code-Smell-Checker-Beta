package inspection;

import com.intellij.ide.util.PropertiesComponent;
import org.jetbrains.annotations.NotNull;

/**
 * インスペクションの検出条件に関するデータクラス
 */
public class InspectionData {
    public enum InspectionDataKey {
        LONG_METHOD,
        LONG_PARAMETER_LIST,
        MESSAGE_CHAINS,
        NULL
    }

    private static InspectionData longMethodInspectionData = new InspectionData("limit value used for LongMethodInspection", 30);
    private static InspectionData longParameterInspectionData = new InspectionData("limit value used for LongParameterListInspection", 5);
    private static InspectionData messageChainsInspectionData = new InspectionData("limit value used for MessageChainsInspection", 2);

    private String  componentName;
    private int componentValue;

    private InspectionData(String  componentName, int componentValue) {
        this.componentName = componentName;
        this.componentValue = componentValue;
    }

    public static InspectionData getInstance(@NotNull InspectionDataKey key) {
        switch (key) {
            case LONG_METHOD:
                return longMethodInspectionData;

            case LONG_PARAMETER_LIST:
                return longParameterInspectionData;

            case MESSAGE_CHAINS:
                return messageChainsInspectionData;

            default:
                return null;
        }
    }

    public String getComponentName() {
        return componentName;
    }

    public int getUpperLimitValue() {
        String value = PropertiesComponent.getInstance().getValue(componentName);
        if (value != null) {
            return Integer.parseInt(value);
        }
        return componentValue;
    }
}
