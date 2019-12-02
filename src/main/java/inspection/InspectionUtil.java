package inspection;

import com.intellij.ide.util.PropertiesComponent;
import org.jetbrains.annotations.NotNull;
import ui.inspectionOptions.listener.OptionButtonListener;
import ui.inspectionOptions.InspectionOptionUI;

import javax.swing.*;

import static ui.inspectionOptions.InspectionOptionsUtil.TOO_SMALL_VALUE;

/**
 * 各インスペクションに関する処理で共通の処理を書くクラス
 */
public class InspectionUtil {
  public static final int DEFAULT_NUM_PARAMETER_LIST = 5;
  public static final int DEFAULT_NUM_PROCESSES = 5;
  public static final int DEFAULT_NUM_CHAINS = 3;

  public final static String LONG_METHOD_PROPERTIES_COMPONENT_NAME = "limit value used for LongMethodInspection";
  public final static String LONG_PARAMETER_LIST_PROPERTIES_COMPONENT_NAME = "limit value used for LongParameterListInspection";
  public final static String MESSAGE_CHAINS_PROPERTIES_COMPONENT_NAME = "limit value used for MessageChainsInspection";

  public final static String HAS_WORKED_LONG_METHOD_INSPECTION_PROPERTIES_COMPONENT_NAME = "is enable LongMethodInspection";
  public final static String HAS_WORKED_LONG_PARAMETER_LIST_INSPECTION_PROPERTIES_COMPONENT_NAME = "is enable LongParameterInspection";
  public final static String HAS_WORKED_MESSAGE_CHAINS_INSPECTION_PROPERTIES_COMPONENT_NAME = "is enable MessageChainsInspection";

  public final static boolean IS_ENABLED_BY_DEFAULT = false;

  public static final String GROUP_NAME = "Code smell";

  public static int getUpperLimitValue(String valueName, int DefaultValue) {
    String value = PropertiesComponent.getInstance().getValue(valueName);
    if (value != null) {
      return Integer.parseInt(value);
    } else {
      return DefaultValue;
    }
  }

  public static int getUpperLimitValue(@NotNull InspectionData data) {
    String value = PropertiesComponent.getInstance().getValue(data.getComponentName());
    if (value != null) {
      return Integer.parseInt(value);
    } else {
      return data.getComponentValue();
    }
  }

  public static JComponent createOptionUI(String description, @NotNull InspectionData data) {
    String successMessage = "save" + description;

    InspectionOptionUI optionUI = new InspectionOptionUI(description, getUpperLimitValue(data.getComponentName(), data.getComponentValue()));
    OptionButtonListener listener = new OptionButtonListener(optionUI.getTextField(), successMessage, TOO_SMALL_VALUE, data.getComponentName());

    return optionUI.createOptionPanel(listener);
  }

  public static boolean getWorkedInspection(String propertiesComponentName) {
    String value = PropertiesComponent.getInstance().getValue(propertiesComponentName);
    if (value != null) {
      return Boolean.valueOf(value);
    } else {
      return IS_ENABLED_BY_DEFAULT;
    }
  }
}
