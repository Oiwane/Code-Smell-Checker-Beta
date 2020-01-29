package inspection.refactoring.hideDelegate;

import com.intellij.codeInsight.daemon.impl.analysis.JavaHighlightUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.refactoring.HelpID;
import com.intellij.refactoring.extractMethod.ExtractMethodHandler;
import com.intellij.refactoring.extractMethod.ExtractMethodProcessor;
import com.intellij.refactoring.extractMethod.ExtractMethodSnapshot;
import com.intellij.refactoring.extractMethod.PrepareFailedException;
import com.intellij.refactoring.extractMethod.preview.ExtractMethodPreviewManager;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.refactoring.util.duplicates.DuplicatesImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

class HideDelegateExtractMethodHandler extends ExtractMethodHandler {
  static HideDelegateExtractMethodProcessor getProcessor(final Project project,
                                                         final PsiElement[] elements,
                                                         final PsiFile file,
                                                         final boolean openEditor,
                                                         PsiReferenceExpression base) {
    return getProcessor(elements, project, file, openEditor ? openEditor(file) : null, base);
  }

  private static HideDelegateExtractMethodProcessor getProcessor(final PsiElement[] elements,
                                                                 final Project project,
                                                                 final PsiFile file,
                                                                 final Editor editor,
                                                                 PsiReferenceExpression base) {
    if (elements == null || elements.length == 0) {
      return null;
    }

    for (PsiElement element : elements) {
      if (element instanceof PsiStatement && JavaHighlightUtil.isSuperOrThisCall((PsiStatement)element, true, true)) {
        return null;
      }
    }

    String initialMethodName = Optional.ofNullable(ExtractMethodSnapshot.SNAPSHOT_KEY.get(file)).map(s -> s.myMethodName).orElse("");
    final HideDelegateExtractMethodProcessor processor =
            new HideDelegateExtractMethodProcessor(project, editor, elements, null, REFACTORING_NAME, initialMethodName, HelpID.EXTRACT_METHOD, base);
    processor.setShowErrorDialogs(false);
    try {
      if (!processor.prepare(null)) return null;
    }
    catch (PrepareFailedException e) {
      return null;
    }
    return processor;
  }

  public static boolean invokeOnElements(final Project project, @NotNull final ExtractMethodProcessor processor, final PsiFile file, final boolean directTypes) {
//    openEditor(file);
    if (!CommonRefactoringUtil.checkReadOnlyStatus(project, processor.getTargetClass().getContainingFile())) return false;

    processor.setPreviewSupported(true);
    if (processor.showDialog(directTypes)) {
      if (processor.isPreviewDuplicates()) {
        previewExtractMethod(processor);
        return true;
      }
      extractMethod(project, processor);
      DuplicatesImpl.processDuplicates(processor, project, openEditor(file));
      return true;
    }
    return false;
  }

  private static void previewExtractMethod(@NotNull ExtractMethodProcessor processor) {
    processor.previewRefactoring(null);
    ExtractMethodPreviewManager.getInstance(processor.getProject()).showPreview(processor);
  }
}
