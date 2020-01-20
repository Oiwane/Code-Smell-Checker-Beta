package inspection.codeSmell;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import inspection.CodeSmellInspection;
import inspection.InspectionData;
import inspection.InspectionSettingName;
import inspection.InspectionSettingValue;
import inspection.InspectionUtil;
import inspection.refactoring.hideDelegate.HideDelegate;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * コードスメル『Message Chains（メッセージの連鎖）』のインスペクション
 */
public class MessageChainsInspection extends CodeSmellInspection {
  private LocalQuickFix quickFix = new HideDelegate();

  public MessageChainsInspection() {
    inspectionData = new InspectionData(InspectionSettingName.MESSAGE_CHAINS_PROPERTIES_COMPONENT_NAME, InspectionSettingValue.DEFAULT_NUM_CHAINS);
    upperLimitValue = InspectionUtil.getUpperLimitValue(inspectionData);
  }

  @Override
  @NotNull
  public String getDisplayName() {
      return "Message chains";
  }

  @NonNls
  @NotNull
  public String getShortName() {
    return "MessageChainsInspection";
  }

  @Override
  public JComponent createOptionsPanel() {
    String description = "detected length of \"" + getDisplayName() + "\"";
    return this.createOptionUI(description, inspectionData);
  }

  private ProblemDescriptor[] checkReferenceExpression(@NotNull PsiReferenceExpression expression, @NotNull InspectionManager manager, boolean isOnTheFly) {
    return checkExpression(expression, manager, isOnTheFly);
  }

  private ProblemDescriptor[] checkMethodCallExpression(@NotNull PsiMethodCallExpression expression, @NotNull InspectionManager manager, boolean isOnTheFly) {
    return checkExpression(expression, manager, isOnTheFly);
  }

  @Nullable
  private ProblemDescriptor[] checkExpression(@NotNull PsiExpression expression, @NotNull InspectionManager manager, boolean isOnTheFly) {
    if (expression.getParent() instanceof PsiExpression) return null;

    int count = countPsiExpression(expression) - 1;

    upperLimitValue = InspectionUtil.getUpperLimitValue(inspectionData);
    if (count <= upperLimitValue) return null;

    List<ProblemDescriptor> descriptors = new ArrayList<>();
    descriptors.add(manager.createProblemDescriptor(expression, getDisplayName() + " : length of chain is " + count, quickFix, ProblemHighlightType.WARNING, isOnTheFly));

    return descriptors.toArray(new ProblemDescriptor[0]);
  }

  private int countPsiExpression(@NotNull PsiExpression expression) {
    for (PsiElement element : expression.getChildren()) {
      if (element instanceof PsiReferenceExpression) {
        return countPsiExpression((PsiExpression) element) + 1;
      } else if (element instanceof PsiMethodCallExpression) {
        PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) element;
        return countPsiExpression(methodCallExpression.getMethodExpression()) + 1;
      }
    }

    return 0;
  }

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
    return new JavaElementVisitor() {

      @Override
      public void visitReferenceExpression(PsiReferenceExpression expression) {
        super.visitReferenceExpression(expression);

        addDescriptors(checkReferenceExpression(expression, holder.getManager(), isOnTheFly));
      }

      @Override
      public void visitMethodCallExpression(PsiMethodCallExpression expression) {
        super.visitMethodCallExpression(expression);

        addDescriptors(checkMethodCallExpression(expression, holder.getManager(), isOnTheFly));
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

  private boolean isMethodCallExpression(@NotNull PsiElement element) {
    return element instanceof PsiMethodCallExpression;
  }

  private boolean isReferenceExpression(@NotNull PsiElement element) {
    return element instanceof PsiReferenceExpression;
  }
}
