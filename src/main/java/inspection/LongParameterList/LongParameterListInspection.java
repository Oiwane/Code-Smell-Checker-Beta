package inspection.LongParameterList;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import inspection.Setting;
import org.jetbrains.annotations.NotNull;

public class LongParameterListInspection extends AbstractBaseJavaLocalInspectionTool {
  private final LocalQuickFix quickFix = new LongParameterListFix();
  private int numParameterList;

  public LongParameterListInspection() {
    numParameterList = Setting.numParameterList;
    System.out.println("LongParameterListInspection start");
  }

//  @Override
  @NotNull
  public String getDisplayName() {
      return "Long parameter list";
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
        System.out.println(list);

        super.visitParameterList(list);
        if (list.getParametersCount() < numParameterList) {
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
