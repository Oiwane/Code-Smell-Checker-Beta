package inspection.LongParameterList;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static inspection.InspectionSetting.DEFAULT_NUM_PARAMETER_LIST;
import static inspection.InspectionSetting.GROUP_NAME;

public class LongParameterListInspection extends AbstractBaseJavaLocalInspectionTool {
  private final LocalQuickFix quickFix = new LongParameterListFix();
  private int numParameterList;

  public LongParameterListInspection() {
    numParameterList = DEFAULT_NUM_PARAMETER_LIST;
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

        if (list.getParametersCount() <= numParameterList) {
          return;
        }

        registerError(holder, list);
      }
    };
  }

  @NotNull
  public HighlightDisplayLevel getDefaultLevel() {
    return HighlightDisplayLevel.WARNING;
  }
}
