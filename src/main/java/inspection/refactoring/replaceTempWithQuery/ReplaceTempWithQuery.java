package inspection.refactoring.replaceTempWithQuery;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.refactoring.extractMethod.ExtractMethodHandler;
import com.intellij.refactoring.extractMethod.ExtractMethodProcessor;
import inspection.refactoring.replaceTempWithQuery.DeclarationStatementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class ReplaceTempWithQuery implements LocalQuickFix {
  @Nls(capitalization = Nls.Capitalization.Sentence)
  @NotNull
  @Override
  public String getFamilyName() {
    return "Replace temp with query";
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    PsiMethod method = (PsiMethod) descriptor.getPsiElement().getParent();
    DeclarationStatementVisitor declarationStatementVisitor = new DeclarationStatementVisitor();
    method.accept(declarationStatementVisitor);

    for (PsiDeclarationStatement statement : declarationStatementVisitor.getDeclarationStatementList()) {
      PsiElement[] elements = new PsiElement[]{statement};
      ExtractMethodProcessor processor = ExtractMethodHandler.getProcessor(project, elements, method.getContainingFile(), false);
      assert processor != null;

      ApplicationManager.getApplication().invokeLater(() -> {
        if (ExtractMethodHandler.invokeOnElements(project, processor, method.getContainingFile(), true)) return;
        // DeclarationStatementVisitorで制限しているので配列の中身は1要素しか無い
        for (PsiElement child : statement.getChildren()) {
          if (child instanceof PsiLocalVariable) return;

          PsiLocalVariable localVariable = (PsiLocalVariable) child;
          // TODO : 以下、未完成
          PsiElement replacedElement = localVariable.getReference().resolve();
          WriteCommandAction.runWriteCommandAction(project, () -> {
            replacedElement.replace(processor.getMethodCall());
          });
        }
      });
    }
  }

}
