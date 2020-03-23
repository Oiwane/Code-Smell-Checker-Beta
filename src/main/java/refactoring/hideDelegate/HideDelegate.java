package refactoring.hideDelegate;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiVariable;
import psi.PsiUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HideDelegate implements LocalQuickFix {
  public static final String QUICK_FIX_NAME = "Hide Delegate";

  @Nls(capitalization = Nls.Capitalization.Sentence)
  @NotNull
  @Override
  public String getFamilyName() {
    return QUICK_FIX_NAME;
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    ApplicationManager.getApplication().invokeLater(() -> {
      CommandProcessor.getInstance().executeCommand(project, () -> {
        PsiExpression expression = (PsiExpression) descriptor.getPsiElement();
        PsiReferenceExpression base = PsiUtil.findBaseElement(expression);
        // 一番最初の要素の定義場所を取得
        PsiElement definition = base.getReference().resolve();

        if (hasStatic(definition)) return;

        PsiClass classInsertedMethod = getClassInsertedMethod(definition);
        if (classInsertedMethod == null || !classInsertedMethod.isWritable()) return;

        PsiElement[] elements = new PsiElement[]{expression};
        HideDelegateExtractMethodProcessor processor = HideDelegateExtractMethodHandler.getProcessor(project, elements, expression.getContainingFile(), base);
        assert processor != null;

        TransactionGuard.getInstance().submitTransactionAndWait(() -> {
          if (HideDelegateExtractMethodHandler.invokeOnElements(project, processor, expression.getContainingFile(), true)) {
            createNewMethod(processor);
            PsiMethod method = processor.getExtractedMethod();
            final PsiClass containingClass = method.getContainingClass();
            if (classInsertedMethod.equals(containingClass)) return;

            WriteCommandAction.runWriteCommandAction(project, () -> {
              PsiElementFactory factory = PsiElementFactory.getInstance(project);

              final PsiMethodCallExpression methodCall = processor.getMethodCall();
              PsiExpression newElement = factory.createExpressionFromText(base.getText() + "." + methodCall.getText(), null);
              methodCall.replace(newElement);

              PsiMethod newMethod = PsiUtil.cloneMethod(processor.getExtractedMethod());
              processor.getExtractedMethod().delete();

              checkAccessModifier(classInsertedMethod, getClassInsertedMethod(expression), newMethod);
              classInsertedMethod.add(newMethod);
            });
          }
        });
      }, "hide delegate", getFamilyName());
    });
  }

  private boolean hasStatic(PsiElement definition) {
    if (definition instanceof PsiField) {
      PsiField field = (PsiField) definition;
      return field.getModifierList() != null && field.getModifierList().hasExplicitModifier(PsiModifier.STATIC);
    } else if (definition instanceof PsiMethod) {
      PsiMethod method = (PsiMethod) definition;
      return method.getModifierList().hasExplicitModifier(PsiModifier.STATIC);
    }
    return false;
  }

  private void checkAccessModifier(@NotNull PsiClass classInsertedMethod, PsiClass originalClass, PsiMethod newMethod) {
    PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(classInsertedMethod.getProject());
    if (!classInsertedMethod.equals(originalClass)) {
      for (PsiElement child : newMethod.getModifierList().getChildren()) {
        if (child.getText().equals(PsiModifier.PRIVATE)) {
          child.replace(factory.createKeyword(PsiModifier.PUBLIC));
          break;
        }
      }
    }
  }

  private void createNewMethod(@NotNull HideDelegateExtractMethodProcessor processor) {
    WriteCommandAction.runWriteCommandAction(processor.getProject(), () -> {
      PsiMethod method = processor.getExtractedMethod();
      PsiStatement statement = method.getBody().getStatements()[0];

      PsiExpression psiExpression;
      if (statement instanceof PsiReturnStatement) {
        PsiReturnStatement returnStatement = (PsiReturnStatement) statement;
        psiExpression = returnStatement.getReturnValue();
      } else {
        PsiExpressionStatement expressionStatement = (PsiExpressionStatement) statement;
        psiExpression = expressionStatement.getExpression();
      }
      // 一番最初の要素を取得
      PsiReferenceExpression baseElement = PsiUtil.findBaseElement(psiExpression);

      // 新しいメソッドを追加するクラスの特定
      baseElement.delete();
    });
  }

  @Nullable
  private PsiClass getClassInsertedMethod(PsiElement baseElementDefinitionElement) {
    PsiClass classInsertedMethod = null;
    if (baseElementDefinitionElement instanceof PsiVariable) {
      PsiVariable variable = (PsiVariable) baseElementDefinitionElement;
      PsiElement element = variable.getTypeElement().getChildren()[0];
      if (element instanceof PsiJavaCodeReferenceElement) {
        PsiJavaCodeReferenceElement javaCodeReferenceElement = (PsiJavaCodeReferenceElement) element;
        classInsertedMethod = (PsiClass) javaCodeReferenceElement.getReference().resolve();
      } else return null;
    } else if (baseElementDefinitionElement instanceof PsiClass) {
      classInsertedMethod = (PsiClass) baseElementDefinitionElement;
    }
    return classInsertedMethod;
  }

}
