package inspection.longMethod;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.*;
import com.intellij.psi.*;
import inspection.InspectionData;
import inspection.InspectionUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static inspection.InspectionUtil.*;
import static psi.PsiUtil.countStatement;

/**
 * コードスメル『Long Method（長いメソッド）』のインスペクション
 */
public class LongMethodInspection extends AbstractBaseJavaLocalInspectionTool {
  private LocalQuickFix quickFix = new LongMethodFix();
  private int numProcesses;

  public LongMethodInspection() {
    numProcesses = getUpperLimitValue(LONG_METHOD_PROPERTIES_COMPONENT_NAME, DEFAULT_NUM_PROCESSES);
  }

  @Override
  @NotNull
  public String getDisplayName() {
    return "Long method";
  }

  @NonNls
  @NotNull
  public String getShortName() {
    return "LongMethodInspection";
  }

  @NotNull
  public String getGroupDisplayName() {
    return GROUP_NAME;
  }

  public boolean isEnabledByDefault() {
    return true;
  }

  @NotNull
  public HighlightDisplayLevel getDefaultLevel() {
    return HighlightDisplayLevel.WARNING;
  }

  @Override
  public JComponent createOptionsPanel() {
    String description = "detected length of \"" + getDisplayName() + "\"";
    InspectionData defaultData = new InspectionData(LONG_METHOD_PROPERTIES_COMPONENT_NAME, DEFAULT_NUM_PROCESSES);

    return InspectionUtil.createOptionUI(description, defaultData);
  }

  @Override
  @Nullable
  public ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
    if (method.getBody() == null) {
      return null;
    }

    numProcesses = getUpperLimitValue(LONG_METHOD_PROPERTIES_COMPONENT_NAME, DEFAULT_NUM_PROCESSES);
    if (countStatement(method) <= numProcesses) {
      return null;
    }

    List<ProblemDescriptor> descriptors = new ArrayList<>();
    descriptors.add(manager.createProblemDescriptor(method, getDisplayName(), quickFix, ProblemHighlightType.WARNING, isOnTheFly));

    return descriptors.toArray(new ProblemDescriptor[0]);
  }

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
    return new JavaElementVisitor() {

      @Override
      public void visitMethod(PsiMethod method) {
        super.visitMethod(method);

        addDescriptors(checkMethod(method, holder.getManager(), isOnTheFly));
      }

      private void addDescriptors(final ProblemDescriptor[] descriptors) {
        if (descriptors != null) {
          for (ProblemDescriptor descriptor : descriptors) {
            holder.registerProblem(descriptor);
          }
        }
      }
    };
  }
}