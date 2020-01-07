package inspection.codeSmell;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiParameterList;
import inspection.CodeSmellInspection;
import inspection.InspectionData;
import inspection.InspectionSettingName;
import inspection.InspectionSettingValue;
import inspection.InspectionUtil;
import inspection.refactoring.introduceParameterObject.IntroduceParameterObject;
import inspection.refactoring.preserveWholeObject.PreserveWholeObject;
import inspection.refactoring.replaceParameterWithMethod.ReplaceParameterWithMethod;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * コードスメル『Long Parameter List（長いパラメータリスト）』のインスペクション
 */
public class LongParameterListInspection extends CodeSmellInspection {
  private final LocalQuickFix replaceParameterWithMethod = new ReplaceParameterWithMethod();
  private final LocalQuickFix introduceParameterObject = new IntroduceParameterObject();
  private final LocalQuickFix preserveWholeObject = new PreserveWholeObject();
  private InspectionData inspectionData;
  private int numParameterList;

  public LongParameterListInspection() {
    inspectionData = new InspectionData(InspectionSettingName.LONG_PARAMETER_LIST_PROPERTIES_COMPONENT_NAME, InspectionSettingValue.DEFAULT_NUM_PARAMETER_LIST);
    numParameterList = InspectionUtil.getUpperLimitValue(inspectionData);
  }

  @Override
  @NotNull
  public String getDisplayName() {
      return "Long parameter list";
  }

  @NonNls
  @NotNull
  public String getShortName() {
    return "LongParameterListInspection";
  }

  @Override
  public JComponent createOptionsPanel() {
    String description = "detected length of \"" + getDisplayName() + "\"";
    return this.createOptionUI(description, inspectionData);
  }

  @Nullable
  private ProblemDescriptor[] checkParameterList(@NotNull PsiParameterList list, @NotNull InspectionManager manager, boolean isOnTheFly) {
    numParameterList = InspectionUtil.getUpperLimitValue(inspectionData);
    if (list.getParametersCount() <= numParameterList) {
      return null;
    }

    List<ProblemDescriptor> descriptors = new ArrayList<>();
    descriptors.add(manager.createProblemDescriptor(
            list, list, getDisplayName(), ProblemHighlightType.WARNING, isOnTheFly,
            replaceParameterWithMethod, introduceParameterObject, preserveWholeObject));

    return descriptors.toArray(new ProblemDescriptor[0]);
  }

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
    return new JavaElementVisitor() {
      @Override
      public void visitParameterList(PsiParameterList list) {
        super.visitParameterList(list);

        addDescriptors(checkParameterList(list, holder.getManager(), isOnTheFly));
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
