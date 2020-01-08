package inspection.refactoring.hideDelegate;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
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
    PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) descriptor.getPsiElement();

    // a.hoge().fuga() -> aのクラス特定して、fuga()の戻り値のオブジェクトを返すgetterメソッドを作成
    // aクラスのファイルが変更可能かを調べる
    // 可能ならばgetterを作成
    PsiReferenceExpression baseObject = findBaseObject(methodCallExpression);
    if (baseObject == null) return;

    PsiElement tempElement = baseObject.getReference().resolve();

    assert tempElement instanceof PsiVariable;
//    if (!(tempElement instanceof PsiVariable)) return;

    PsiVariable variable = (PsiVariable) tempElement;
    PsiJavaCodeReferenceElement javaCodeReferenceElement = variable.getTypeElement().getInnermostComponentReferenceElement();
    PsiElement sourceElement = javaCodeReferenceElement.getReference().resolve();

    if (!sourceElement.isWritable()) return;

    PsiReference referenceOfMethodChainsTail = methodCallExpression.getMethodExpression().getReference();
    tempElement = referenceOfMethodChainsTail.resolve();

    if (!(tempElement instanceof PsiMethod)) return;

    PsiMethod method = (PsiMethod) tempElement;
    PsiTypeElement returnTypeElement = method.getReturnTypeElement();

    ApplicationManager.getApplication().invokeLater(() -> {
      WriteCommandAction.runWriteCommandAction(project, () -> {
        PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(project);
        PsiMethod newMethod = factory.createMethod("get" + returnTypeElement.getInnermostComponentReferenceElement().getQualifiedName(), returnTypeElement.getType());
        PsiCodeBlock newCodeBlock = newMethod.getBody();
        String returnStatementStr = "return " + methodCallExpression.getText().substring((baseObject.getText() + ".").length());
        PsiReturnStatement returnStatement = (PsiReturnStatement) factory.createStatementFromText(returnStatementStr, newCodeBlock);
        newCodeBlock.add(returnStatement);
        method.getContainingClass().add(newMethod);
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
}
