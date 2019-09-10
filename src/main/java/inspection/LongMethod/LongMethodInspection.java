package inspection.LongMethod;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.*;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.psi.*;
import inspection.InspectionSetting;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import psi.PsiUtil;
import ui.inspectionOptions.LongMethodInspectionOption;

import javax.swing.*;

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
      return InspectionSetting.numLines;
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
    return "LongMethodInspector";
  }

  @NotNull
  public String getGroupDisplayName() {
    return "Code Smell";
  }

  public boolean isEnabledByDefault() {
    return true;
  }

  @NotNull
  public HighlightDisplayLevel getDefaultLevel() {
    return HighlightDisplayLevel.WARNING;
  }

  private void registerError(ProblemsHolder holder, PsiElement element) {
    holder.registerProblem(element, this.getDisplayName(), quickFix);
  }

  @Override
  public JComponent createOptionsPanel() {
    LongMethodInspectionOption option = new LongMethodInspectionOption();

    return option.createOptionPanel();
  }

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
    return new JavaElementVisitor() {

      @Override
      public void visitMethod(PsiMethod method) {
        super.visitMethod(method);

        System.out.println(method.getContainingFile().toString());
        System.out.println(method.toString());
        System.out.println(method.getParameterList().toString());
        if (method.getBody() == null) {
          System.out.println("null");
          return;
        }

        int count = PsiUtil.countStatement(method);
        System.out.println("statement total : " + count + "\n");

        numLines = initNumOfLine();
        if (count <= numLines) {
          return;
        }

        registerError(holder, method);
      }
    };
  }
}