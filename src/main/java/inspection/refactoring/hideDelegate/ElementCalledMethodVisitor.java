package inspection.refactoring.hideDelegate;

import com.intellij.psi.JavaRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiReferenceExpression;

public class ElementCalledMethodVisitor extends JavaRecursiveElementWalkingVisitor {
  private PsiReferenceExpression objectElement;

  @Override
  public void visitReferenceExpression(PsiReferenceExpression expression) {
    super.visitReferenceExpression(expression);
    if (expression.getParent() instanceof PsiReferenceExpression) objectElement = (PsiReferenceExpression) expression.getParent();
  }

  public PsiReferenceExpression getObjectElement() {
    return objectElement;
  }
}
