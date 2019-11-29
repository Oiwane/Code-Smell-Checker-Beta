package inspection.longParameterList;

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

/**
 * コードスメル『Long Parameter List（長いパラメータリスト）』のインスペクション
 */
public class LongParameterListInspection extends CodeSmellInspection {
  private final LocalQuickFix quickFix = new LongParameterListFix();
  private int numParameterList;

  public LongParameterListInspection() {
    numParameterList = InspectionUtil.getUpperLimitValue(InspectionUtil.LONG_PARAMETER_LIST_PROPERTIES_COMPONENT_NAME,
                                                         InspectionUtil.DEFAULT_NUM_PARAMETER_LIST);
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
  public String getWorked() {
    return InspectionUtil.HAS_WORKED_LONG_PARAMETER_LIST_INSPECTION_PROPERTIES_COMPONENT_NAME;
  }

  @Override
  public JComponent createOptionsPanel() {
    String description = "detected length of \"" + getDisplayName() + "\"";
    InspectionData defaultData = new InspectionData(InspectionUtil.LONG_PARAMETER_LIST_PROPERTIES_COMPONENT_NAME,
                                                    InspectionUtil.DEFAULT_NUM_PARAMETER_LIST);

    return InspectionUtil.createOptionUI(description, defaultData);
  }

  @Nullable
  public ProblemDescriptor[] checkParameterList(@NotNull PsiParameterList list, @NotNull InspectionManager manager, boolean isOnTheFly) {
    numParameterList = InspectionUtil.getUpperLimitValue(InspectionUtil.LONG_PARAMETER_LIST_PROPERTIES_COMPONENT_NAME,
                                                         InspectionUtil.DEFAULT_NUM_PARAMETER_LIST);
    if (list.getParametersCount() <= numParameterList) {
      return null;
    }

    List<ProblemDescriptor> descriptors = new ArrayList<>();
    descriptors.add(manager.createProblemDescriptor(list, getDisplayName(), quickFix, ProblemHighlightType.WARNING, isOnTheFly));

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
