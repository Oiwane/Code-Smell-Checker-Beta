package inspection.codeSmell.longMethod;

import com.intellij.codeInspection.*;
import com.intellij.psi.*;
import inspection.*;
import inspection.refactoring.DecomposeConditional;
import inspection.refactoring.ExtractMethod;
import inspection.refactoring.ReplaceMethodWithMethodObject;
import inspection.refactoring.ReplaceTempWithQuery;
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
  private LocalQuickFix replaceTempWithQuery = new ReplaceTempWithQuery();
  private LocalQuickFix decomposeConditional = new DecomposeConditional();
  private LocalQuickFix replaceMethodWithMethodObject = new ReplaceMethodWithMethodObject();
  private LocalQuickFix extractMethod = new ExtractMethod();
  private InspectionData inspectionData;
  private int numProcesses;

  public LongMethodInspection() {
    inspectionData = new InspectionData(InspectionSettingName.LONG_METHOD_PROPERTIES_COMPONENT_NAME, InspectionSettingValue.DEFAULT_NUM_PROCESSES);
    numProcesses = InspectionUtil.getUpperLimitValue(inspectionData);
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
    return InspectionState.LONG_METHOD_INSPECTION_STATE_PROPERTIES_COMPONENT_NAME.getName();
  }

  @Override
  public JComponent createOptionsPanel() {
    String description = "detected length of \"" + getDisplayName() + "\"";
    return this.createOptionUI(description, inspectionData);
  }

  @Override
  @Nullable
  public ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
    if (method.getBody() == null) {
      return null;
    }

    numProcesses = InspectionUtil.getUpperLimitValue(inspectionData);
    if (countStatement(method) <= numProcesses) {
      return null;
    }

    final PsiIdentifier identifier = method.getNameIdentifier();
    if (identifier == null) return null;

    List<ProblemDescriptor> descriptors = new ArrayList<>();
    descriptors.add(manager.createProblemDescriptor(
            identifier, identifier, getDisplayName(), ProblemHighlightType.WARNING, isOnTheFly,
            replaceTempWithQuery, decomposeConditional, replaceMethodWithMethodObject, extractMethod
    ));

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