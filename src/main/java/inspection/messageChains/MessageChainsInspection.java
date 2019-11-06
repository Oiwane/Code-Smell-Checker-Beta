package inspection.messageChains;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.*;
import com.intellij.psi.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ui.inspectionOptions.InspectionOptionListener;
import ui.inspectionOptions.InspectionOptionUI;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;

import static inspection.InspectionUtil.*;
import static psi.PsiUtil.countPsiMethodCallExpression;
import static ui.inspectionOptions.InspectionOptionsUtil.TOO_SMALL_VALUE;

/**
 * コードスメル『Message Chains（メッセージの連鎖）』のインスペクション
 */
public class MessageChainsInspection extends AbstractBaseJavaLocalInspectionTool {
  private LocalQuickFix quickFix = new MessageChainsFix();
  private int numChains;

  public MessageChainsInspection() {
    numChains = getUpperLimitValue(MESSAGE_CHAINS_PROPERTIES_COMPONENT_NAME, DEFAULT_NUM_CHAINS);
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
    String description = "detected length of \"" + getDisplayName() + "\" : ";
    String successMessage = "save " + description;

    InspectionOptionUI optionUI = new InspectionOptionUI(description, getUpperLimitValue(MESSAGE_CHAINS_PROPERTIES_COMPONENT_NAME, DEFAULT_NUM_CHAINS));
    InspectionOptionListener listener = new InspectionOptionListener(optionUI.getSpinnerNumberModel(), successMessage, TOO_SMALL_VALUE, MESSAGE_CHAINS_PROPERTIES_COMPONENT_NAME);

    return optionUI.createOptionPanel(listener);
  }

  @Nullable
  public ProblemDescriptor[] checkMethodCallExpression(@NotNull PsiMethodCallExpression expression, @NotNull InspectionManager manager, boolean isOnTheFly) {
    int count;
    // 木の途中でないかを確認
    if (isReferenceExpression(expression.getParent()) && isMethodCallExpression(expression.getParent().getParent())) {
      return null;
    } else {
      count = countPsiMethodCallExpression(expression);
    }

    numChains = getUpperLimitValue(MESSAGE_CHAINS_PROPERTIES_COMPONENT_NAME, DEFAULT_NUM_CHAINS);
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
