package inspection.LongMethod;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.*;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.psi.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import psi.PsiUtil;
import ui.inspectionOptions.LongMethodInspectionOption;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static inspection.InspectionSetting.DEFAULT_NUM_LINES;
import static inspection.InspectionSetting.GROUP_NAME;

public class LongMethodInspection extends AbstractBaseJavaLocalInspectionTool {
  private LocalQuickFix quickFix = new LongMethodFix();
  private int numLines;

  public LongMethodInspection() {
    numLines = initNumOfLine();
  }

  public static int initNumOfLine() {
    String value = PropertiesComponent.getInstance().getValue("value of LongMethodInspection");
    if (value != null) {
      return Integer.parseInt(value);
    } else {
      return DEFAULT_NUM_LINES;
    }
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
    LongMethodInspectionOption option = new LongMethodInspectionOption();

    return option.createOptionPanel();
  }

  @Override
  @Nullable
  public ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
    if (method.getBody() == null) {
      return null;
    }

    numLines = initNumOfLine();
    if (PsiUtil.countStatement(method) <= numLines) {
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