package inspection.refactoring.hideDelegate;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.refactoring.extractMethod.ExtractMethodHandler;
import com.intellij.refactoring.extractMethod.ExtractMethodProcessor;
import inspection.psi.PsiUtil;
import inspection.refactoring.RefactoringUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HideDelegate implements LocalQuickFix {
  @Nls(capitalization = Nls.Capitalization.Sentence)
  @NotNull
  @Override
  public String getFamilyName() {
    return "Hide delegate";
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    ApplicationManager.getApplication().invokeLater(() -> {
      WriteCommandAction.runWriteCommandAction(project, () -> {
        PsiExpression expression = (PsiExpression) descriptor.getPsiElement();

        PsiElement tailDefinitionElement = null;
        if (expression instanceof PsiReferenceExpression) {
          tailDefinitionElement = expression.getReference().resolve();
        } else if (expression instanceof PsiMethodCallExpression) {
          tailDefinitionElement = ((PsiMethodCallExpression) expression).getMethodExpression().getReference().resolve();
        }
        if (tailDefinitionElement == null) return;

        PsiReferenceExpression baseElement = findBaseElement(expression);
        PsiElement baseElementDefinitionElement = baseElement.getReference().resolve();

        ClassVisitor visitor = new ClassVisitor(baseElementDefinitionElement.getTextRange());
        PsiFile fileContainingChain = baseElementDefinitionElement.getContainingFile();

        fileContainingChain.accept(visitor);
        PsiClass classContainingChain = visitor.getTargetClass();

        PsiType tailElementType = null;
        String newMethodName = null;
        if (tailDefinitionElement instanceof PsiMethod) {
          PsiMethod method = (PsiMethod) tailDefinitionElement;
          tailElementType = method.getReturnType();
          newMethodName = method.getName();
        } else if (tailDefinitionElement instanceof PsiField) {
          PsiField field = (PsiField) tailDefinitionElement;
          tailElementType = field.getType();
          newMethodName = "get" + field.getName();
        }
        if (tailElementType == null) return;

        if (!fileContainingChain.isWritable()) return;
        PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(project);
        PsiMethod newMethod = factory.createMethod(newMethodName, tailElementType);

        PsiStatement statement = null;
        String statementStr = null;
        if (tailElementType.equalsToText("void")) {
          statementStr = "return " + expression.getText().substring(baseElement.getTextLength() + 1, expression.getTextLength());
        } else {
          statementStr = expression.getText().substring(baseElement.getTextLength() + 1, expression.getTextLength());
        }
        statement = factory.createStatementFromText(statementStr, classContainingChain);

        newMethod.getBody().add(statement);
      });
    });
  }

  @Nullable
  private PsiReferenceExpression findBaseObject(@NotNull PsiMethodCallExpression methodCallExpression) {
    for (PsiElement element : methodCallExpression.getMethodExpression().getChildren()) {
      if (element instanceof PsiReferenceExpression) {
        return (PsiReferenceExpression) element;
      } else if (element instanceof PsiMethodCallExpression) {
        return findBaseObject((PsiMethodCallExpression) element);
      }
    }

    return null;
  }

  @Nullable
  private PsiReferenceExpression findBaseElement(@NotNull PsiExpression expression) {
    for (PsiElement element : expression.getChildren()) {
      if (element instanceof PsiReferenceExpression) {
        return findBaseElement((PsiExpression) element);
      } else if (element instanceof PsiMethodCallExpression) {
        PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) element;
        return findBaseElement(methodCallExpression.getMethodExpression());
      }
    }

    return (expression instanceof PsiReferenceExpression) ? (PsiReferenceExpression) expression : null;
  }
}
