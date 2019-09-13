package inspection.MessageChains;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.psi.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ui.inspectionOptions.InspectionOptionListener;
import ui.inspectionOptions.InspectionOptionUI;

import javax.swing.*;

import static inspection.InspectionSetting.*;
import static ui.inspectionOptions.InspectionOptionsUtil.*;

public class MessageChainsInspection extends AbstractBaseJavaLocalInspectionTool {
  private LocalQuickFix quickFix = new MessageChainsFix();
  private int numChains;

  public MessageChainsInspection() {
    numChains = initNumOfChains();
  }

  private static int initNumOfChains() {
    String value = PropertiesComponent.getInstance().getValue(MESSAGE_CHAINS_PROPERTIES_COMPONENT_NAME);
    if (value != null) {
      return Integer.parseInt(value);
    } else {
      return DEFAULT_NUM_CHAINS;
    }
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

  @Override
  public JComponent createOptionsPanel() {
    String description = "detected length of \"" + getDisplayName() + "\" : ";
    String successMessage = "save" + description;

    InspectionOptionUI optionUI = new InspectionOptionUI(description, initNumOfChains());
    InspectionOptionListener listener = new InspectionOptionListener(optionUI.getSpinnerNumberModel(), successMessage, TOO_SMALL_VALUE, MESSAGE_CHAINS_PROPERTIES_COMPONENT_NAME);

    return optionUI.createOptionPanel(listener);
  }

  private void registerError(ProblemsHolder holder, PsiElement element) {
    holder.registerProblem(element, this.getDisplayName(), quickFix);
  }

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
    return new JavaElementVisitor() {

      @Override
      public void visitExpressionStatement(PsiExpressionStatement statement) {
        super.visitExpressionStatement(statement);

        int count = 0;
        // PsiExpressionStatement内のPsiReferenceExpressionの数を比較
        for (PsiElement element : statement.getChildren()) {
          if (element instanceof PsiReferenceExpression) count++;
        }

        if (count <= numChains) return;

        registerError(holder, statement);
      }
    };
  }
}
