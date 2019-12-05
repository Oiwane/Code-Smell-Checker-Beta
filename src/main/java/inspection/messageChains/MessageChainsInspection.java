package inspection.messageChains;

import com.intellij.codeInspection.*;
import com.intellij.psi.*;
import inspection.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;

import static psi.PsiUtil.countPsiMethodCallExpression;

/**
 * コードスメル『Message Chains（メッセージの連鎖）』のインスペクション
 */
public class MessageChainsInspection extends CodeSmellInspection {
  private LocalQuickFix quickFix = new MessageChainsFix();
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
  public String getWorked() {
    return InspectionState.MESSAGE_CHAINS_INSPECTION_STATE_PROPERTIES_COMPONENT_NAME.getName();
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
