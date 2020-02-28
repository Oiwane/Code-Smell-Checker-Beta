package refactoring;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.refactoring.extractMethod.ExtractMethodHandler;
import com.intellij.refactoring.extractMethod.ExtractMethodProcessor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ui.refactoring.extractMethod.SelectExtractedRangeDialog;

/**
 * 未完成のクラス
 */
public class ExtractMethod implements LocalQuickFix {
  @Nls(capitalization = Nls.Capitalization.Sentence)
  @NotNull
  @Override
  public String getFamilyName() {
      return "Extract method";
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    ApplicationManager.getApplication().invokeLater(() -> {
      PsiMethod method = (PsiMethod) descriptor.getPsiElement().getParent();
      SelectExtractedRangeDialog dialog = new SelectExtractedRangeDialog(project, true, method);
      Editor editor = dialog.getEditor();
      dialog.show();
      if (dialog.isOK()) {
        PsiElement[] elements = ExtractMethodHandler.getElements(project, editor, method.getContainingFile());
        ExtractMethodProcessor processor = ExtractMethodHandler.getProcessor(project, elements, method.getContainingFile(), true);
        assert processor != null;

        ExtractMethodHandler.invokeOnElements(project, processor, method.getContainingFile(), true);
      }
    });
  }
}
