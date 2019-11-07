package inspection;

import com.intellij.ide.util.PropertiesComponent;
import org.jetbrains.annotations.NotNull;
import ui.inspectionOptions.InspectionOptionListener;
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
    InspectionOptionListener listener = new InspectionOptionListener(optionUI.getSpinnerNumberModel(), successMessage, TOO_SMALL_VALUE, data.getComponentName());

    return optionUI.createOptionPanel(listener);
  }
}
