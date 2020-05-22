package inspection;

import com.intellij.ide.util.PropertiesComponent;
import org.jetbrains.annotations.NotNull;

/**
 * 各インスペクションに関する処理で共通の処理を書くクラス
 */
public class InspectionUtil {

    public static final boolean IS_ENABLED_BY_DEFAULT = false;
    public static final String GROUP_NAME = "Code smell";

    private InspectionUtil() {
    }

    public static int getUpperLimitValue(@NotNull InspectionData data) {
        String value = PropertiesComponent.getInstance().getValue(data.getComponentName());
        if (value != null) {
            return Integer.parseInt(value);
        } else {
            return data.getComponentValue();
        }
    }
}
