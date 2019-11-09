package inspection.longMethod;

import com.intellij.codeInspection.*;
import com.intellij.psi.*;
import inspection.CodeSmellInspection;
import inspection.InspectionData;
import inspection.InspectionUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static psi.PsiUtil.countStatement;

/**
 * コードスメル『Long Method（長いメソッド）』のインスペクション
 */
public class LongMethodInspection extends CodeSmellInspection {
  private LocalQuickFix quickFix = new LongMethodFix();
  private int numProcesses;

  public LongMethodInspection() {
    numProcesses = InspectionUtil.getUpperLimitValue(InspectionUtil.LONG_METHOD_PROPERTIES_COMPONENT_NAME,
                                                     InspectionUtil.DEFAULT_NUM_PROCESSES);
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

  @Override
  public String getWorked() {
    return InspectionUtil.IS_ENABLED_LONG_METHOD_INSPECTION_PROPERTIES_COMPONENT_NAME;
  }

  @Override
  public JComponent createOptionsPanel() {
    String description = "detected length of \"" + getDisplayName() + "\"";
    InspectionData defaultData = new InspectionData(InspectionUtil.LONG_METHOD_PROPERTIES_COMPONENT_NAME,
                                                    InspectionUtil.DEFAULT_NUM_PROCESSES);

    return InspectionUtil.createOptionUI(description, defaultData);
  }

  @Override
  @Nullable
  public ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
    if (method.getBody() == null) {
      return null;
    }

    numProcesses = InspectionUtil.getUpperLimitValue(InspectionUtil.LONG_METHOD_PROPERTIES_COMPONENT_NAME,
                                                     InspectionUtil.DEFAULT_NUM_PROCESSES);
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