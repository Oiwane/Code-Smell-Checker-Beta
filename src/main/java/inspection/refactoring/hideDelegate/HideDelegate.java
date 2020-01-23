package inspection.refactoring.hideDelegate;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.command.impl.UndoManagerImpl;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.messages.MessageDialog;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiVariable;
import com.intellij.refactoring.extractMethod.ExtractMethodHandler;
import com.intellij.refactoring.extractMethod.ExtractMethodProcessor;
import com.intellij.refactoring.move.moveInstanceMethod.MoveInstanceMethodDialog;
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
      CommandProcessor.getInstance().executeCommand(project, () -> {
        PsiExpression expression = (PsiExpression) descriptor.getPsiElement();
        PsiReferenceExpression base = findBaseElement(expression);
        // 一番最初の要素の定義場所を取得
        PsiElement definition = base.getReference().resolve();

        PsiClass classInsertedMethod = getClassInsertedMethod(definition);
        if (classInsertedMethod == null) return;
        if (!classInsertedMethod.isWritable()) return;

        PsiElement[] elements = new PsiElement[]{expression};
        ExtractMethodProcessor processor = ExtractMethodHandler.getProcessor(project, elements, expression.getContainingFile(), false);
        assert processor != null;

        TransactionGuard.getInstance().submitTransactionAndWait(() -> {
          if (ExtractMethodHandler.invokeOnElements(project, processor, expression.getContainingFile(), true)) {
            createNewMethod(processor);
            PsiMethod method = processor.getExtractedMethod();
            if (classInsertedMethod.equals(method.getContainingClass())) return;

            moveMethod(definition, method);
          }
        });
      }, "hide delegate", getFamilyName());
    });
  }

  private void createNewMethod(@NotNull ExtractMethodProcessor processor) {
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
      PsiReferenceExpression baseElement = findBaseElement(psiExpression);

      // 新しいメソッドを追加するクラスの特定
      baseElement.delete();
    });
  }

  private void moveMethod(PsiElement definition, @NotNull PsiMethod method) {
    Project project = method.getProject();
    TransactionGuard.getInstance().submitTransactionAndWait(() -> {
      if (definition instanceof PsiVariable) {
        MoveInstanceMethodDialog dialog = new MoveInstanceMethodDialog(method, new PsiVariable[]{(PsiVariable) definition});
        dialog.show();
        if (!dialog.isOK()) {
          // TODO マシな方法を見つける
          UndoManagerImpl.getInstance(method.getProject()).undo(FileEditorManager.getInstance(method.getProject()).getSelectedEditor(method.getContainingFile().getVirtualFile()));
          UndoManagerImpl.getInstance(method.getProject()).undo(FileEditorManager.getInstance(method.getProject()).getSelectedEditor(method.getContainingFile().getVirtualFile()));
        }
      } else if (definition instanceof PsiClass) {
        MessageDialog dialog = new MessageDialog("未実装！", "Message", new String[]{"OK"}, 0, null);
        dialog.show();
      }
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
