package inspection;

import com.intellij.ide.util.PropertiesComponent;

public class InspectionUtil {
  public static final int DEFAULT_NUM_PARAMETER_LIST = 5;
  public static final int DEFAULT_NUM_PROCESSES = 5;
  public static final int DEFAULT_NUM_CHAINS = 3;

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
