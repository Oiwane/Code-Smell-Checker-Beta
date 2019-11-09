package inspection;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import org.jetbrains.annotations.NotNull;

public abstract class CodeSmellInspection extends AbstractBaseJavaLocalInspectionTool {

  @NotNull
  public String getGroupDisplayName() {
    return InspectionUtil.GROUP_NAME;
  }

  public boolean isEnabledByDefault() {
    return true;
  }

  @NotNull
  public HighlightDisplayLevel getDefaultLevel() {
    return HighlightDisplayLevel.WARNING;
  }

  public String getWorked() {
    return null;
  }
}
