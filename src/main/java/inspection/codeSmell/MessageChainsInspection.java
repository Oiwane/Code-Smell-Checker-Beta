package inspection.codeSmell;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import inspection.CodeSmellInspection;
import inspection.InspectionData;
import inspection.InspectionSettingName;
import inspection.InspectionSettingValue;
import inspection.InspectionUtil;
import inspection.refactoring.HideDelegate;
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
  private InspectionData inspectionData;
  private int numChains;

  public MessageChainsInspection() {
    inspectionData = new InspectionData(InspectionSettingName.MESSAGE_CHAINS_PROPERTIES_COMPONENT_NAME, InspectionSettingValue.DEFAULT_NUM_CHAINS);
    numChains = InspectionUtil.getUpperLimitValue(inspectionData);
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

  @Nullable
  private ProblemDescriptor[] checkMethodCallExpression(@NotNull PsiMethodCallExpression expression, @NotNull InspectionManager manager, boolean isOnTheFly) {
    int count;
    // 木の途中でないかを確認
    if (isReferenceExpression(expression.getParent()) && isMethodCallExpression(expression.getParent().getParent())) {
      return null;
    } else {
      count = countPsiMethodCallExpression(expression);
    }

    numChains = InspectionUtil.getUpperLimitValue(inspectionData);
    if (count <= numChains) return null;

    List<ProblemDescriptor> descriptors = new ArrayList<>();
    descriptors.add(manager.createProblemDescriptor(expression, getDisplayName(), quickFix, ProblemHighlightType.WARNING, isOnTheFly));

    return descriptors.toArray(new ProblemDescriptor[0]);

  }

  private int countPsiMethodCallExpression(@NotNull PsiMethodCallExpression expression) {
    int count = 1;

    for (PsiElement element : expression.getChildren()) {
      if (element instanceof PsiReferenceExpression) {
        count += countPsiMethodCallExpression((PsiReferenceExpression) element);
      }
    }

    return count;
  }

  private int countPsiMethodCallExpression(@NotNull PsiReferenceExpression expression) {
    int count = 0;

    for (PsiElement element : expression.getChildren()) {
      if (element instanceof PsiMethodCallExpression) {
        count += countPsiMethodCallExpression((PsiMethodCallExpression) element);
      }
    }

    return count;
  }

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
    return new JavaElementVisitor() {

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
