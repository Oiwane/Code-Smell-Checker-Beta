package inspection;

import com.intellij.ide.util.PropertiesComponent;

public class InspectionUtil {
  public static final int DEFAULT_NUM_PARAMETER_LIST = 5;
  public static final int DEFAULT_NUM_PROCESSES = 5;
  public static final int DEFAULT_NUM_CHAINS = 3;

  public final static String LONG_METHOD_PROPERTIES_COMPONENT_NAME = "limit value used for LongMethodInspection";
  public final static String LONG_PARAMETER_LIST_PROPERTIES_COMPONENT_NAME = "limit value used for LongParameterListInspection";
  public final static String MESSAGE_CHAINS_PROPERTIES_COMPONENT_NAME = "limit value used for MessageChainsInspection";

  public static final String GROUP_NAME = "Code smell";

  public static int getUpperLimitValue(String valueName, int DefaultValue) {
    String value = PropertiesComponent.getInstance().getValue(valueName);
    if (value != null) {
      return Integer.parseInt(value);
    } else {
      return DefaultValue;
    }
  }
}
