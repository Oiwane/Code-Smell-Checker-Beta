package inspection;

import com.intellij.ide.util.PropertiesComponent;

import static ui.inspectionOptions.InspectionOptionsUtil.LONG_METHOD_PROPERTIES_COMPONENT_NAME;

public class InspectionSetting {
  public static int DEFAULT_NUM_PARAMETER_LIST = 5;
  public static int DEFAULT_NUM_LINES = 5;
  public static int DEFAULT_NUM_CHAINS = 3;

  public static String GROUP_NAME = "Code Smell";

  public static int getValueFromPropertyComponent(String componentName, int DefaultValue) {
    String value = PropertiesComponent.getInstance().getValue(componentName);
    if (value != null) {
      return Integer.parseInt(value);
    } else {
      return DefaultValue;
    }
  }
}
