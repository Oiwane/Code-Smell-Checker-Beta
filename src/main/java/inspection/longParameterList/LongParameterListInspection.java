package inspection.longParameterList;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ui.inspectionOptions.InspectionOptionListener;
import ui.inspectionOptions.InspectionOptionUI;

import javax.swing.*;

import static inspection.InspectionUtil.*;
import static ui.inspectionOptions.InspectionOptionsUtil.TOO_SMALL_VALUE;

/**
 * コードスメル『Long Parameter List（長いパラメータリスト）』のインスペクション
 */
public class LongParameterListInspection extends AbstractBaseJavaLocalInspectionTool {
  private final LocalQuickFix quickFix = new LongParameterListFix();
  private int numParameterList;

  public LongParameterListInspection() {
    numParameterList = getUpperLimitValue(LONG_PARAMETER_LIST_PROPERTIES_COMPONENT_NAME, DEFAULT_NUM_PARAMETER_LIST);
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

  @NotNull
  public String getGroupDisplayName() {
    return GROUP_NAME;
  }

  @Override
  public JComponent createOptionsPanel() {
    String description = "detected length of \"" + getDisplayName() + "\" : ";
    String successMessage = "save " + description;

    InspectionOptionUI optionUI = new InspectionOptionUI(description, getUpperLimitValue(LONG_PARAMETER_LIST_PROPERTIES_COMPONENT_NAME, DEFAULT_NUM_PARAMETER_LIST));
    InspectionOptionListener listener = new InspectionOptionListener(optionUI.getSpinnerNumberModel(), successMessage, TOO_SMALL_VALUE, LONG_PARAMETER_LIST_PROPERTIES_COMPONENT_NAME);

    return optionUI.createOptionPanel(listener);
  }

  private void registerError(ProblemsHolder holder, PsiElement element) {
    holder.registerProblem(element, getDisplayName(), quickFix);
  }

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
    return new JavaElementVisitor() {
      @Override
      public void visitParameterList(PsiParameterList list) {
        super.visitParameterList(list);

        numParameterList = getUpperLimitValue(LONG_PARAMETER_LIST_PROPERTIES_COMPONENT_NAME, DEFAULT_NUM_PARAMETER_LIST);
        if (list.getParametersCount() <= numParameterList) {
          return;
        }

        registerError(holder, list);
      }
    };
  }
}
