package inspection;

import com.intellij.ide.util.PropertiesComponent;
import org.jetbrains.annotations.NotNull;

/**
 * インスペクションの検出条件に関するデータクラス
 */
public class InspectionData {
    private String  componentName;
    private int componentValue;

    public static final String  LONG_METHOD_PROPERTIES_COMPONENT_NAME = "limit value used for LongMethodInspection";
    public static final String LONG_PARAMETER_LIST_PROPERTIES_COMPONENT_NAME = "limit value used for LongParameterListInspection";
    public static final String MESSAGE_CHAINS_PROPERTIES_COMPONENT_NAME = "limit value used for MessageChainsInspection";
    public static final int DEFAULT_NUM_PARAMETER_LIST = 5;
    public static final int DEFAULT_NUM_STATEMENTS = 30;
    public static final int DEFAULT_NUM_CHAINS = 2;

    public InspectionData(String  componentName, int componentValue) {
        this.componentName = componentName;
        this.componentValue = componentValue;
    }

    String getComponentName() {
        return componentName;
    }

    public int getUpperLimitValue() {
        String value = PropertiesComponent.getInstance().getValue(componentName);
        if (value != null) {
            return Integer.parseInt(value);
        } else {
            return componentValue;
        }
    }
}
