package refactoring.hideDelegate;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import com.intellij.refactoring.extractMethod.ExtractMethodProcessor;
import com.intellij.refactoring.util.duplicates.Match;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class HideDelegateExtractMethodProcessor extends ExtractMethodProcessor {
  private PsiReferenceExpression base;
  private PsiElementFactory myElementFactory;

  HideDelegateExtractMethodProcessor(Project project,
                                     Editor editor,
                                     PsiElement[] elements,
                                     PsiType forcedReturnType,
                                     String refactoringName,
                                     String initialMethodName,
                                     String helpId,
                                     PsiReferenceExpression base) {
    super(project, editor, elements, forcedReturnType, refactoringName, initialMethodName, helpId);
    this.base = base;
    PsiManager myManager = PsiManager.getInstance(myProject);
    myElementFactory = JavaPsiFacade.getElementFactory(myManager.getProject());
  }

  @Override
  public PsiElement processMatch(@NotNull Match match) throws IncorrectOperationException {
    PsiElement replacedMatch = super.processMatch(match);

    final String text = base.getText() + "." + getMethodCall().getText();
    PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) myElementFactory.createExpressionFromText(text, replacedMatch.getContext());

    PsiElement element = match.replace(myExtractedMethod, methodCallExpression, myOutputVariable, myReturnType);

    replacedMatch.replace(element);
    return replacedMatch;
  }
}
