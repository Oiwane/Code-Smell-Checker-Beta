package refactoring.replaceMethodWithMethodObject;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.refactoring.extractMethodObject.ExtractMethodObjectHandler;
import com.intellij.refactoring.extractMethodObject.ExtractMethodObjectProcessor;
import visitor.LocalVariableVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ui.refactoring.replaceMethodWithMethodObject.SelectLocalVariableDialog;

import java.util.ArrayList;
import java.util.List;

public class ReplaceMethodWithMethodObject implements LocalQuickFix {
  @Nls(capitalization = Nls.Capitalization.Sentence)
  @NotNull
  @Override
  public String getFamilyName() {
    return "Replace method with method object";
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    ApplicationManager.getApplication().invokeLater(() -> {
      PsiMethod method = (PsiMethod) descriptor.getPsiElement().getParent();
      LocalVariableVisitor visitor = new LocalVariableVisitor();
      method.accept(visitor);

      List<PsiElement> localVariableList = visitor.getLocalVariableList();
      SelectLocalVariableDialog dialog = new SelectLocalVariableDialog(project, true, localVariableList);
      dialog.show();
      if (dialog.isOK()) {
        List<PsiElement> extractedLocalVariableList = new ArrayList<>();
        for (int index : dialog.getSelectedIndexList()) {
          extractedLocalVariableList.add(localVariableList.get(index));
        }
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        ExtractMethodObjectProcessor processor = new ExtractMethodObjectProcessor(project,
                editor,
                extractedLocalVariableList.toArray(new PsiElement[0]),
                method.getContainingClass().getName());
        ExtractMethodObjectProcessor.MyExtractMethodProcessor extractMethodProcessor = processor.getExtractProcessor();
        assert extractMethodProcessor != null;

        ExtractMethodObjectHandler.extractMethodObject(project,editor, processor, extractMethodProcessor);
      }
    });
  }
}
