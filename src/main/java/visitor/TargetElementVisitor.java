package visitor;

import com.intellij.psi.JavaRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceExpression;

import java.util.ArrayList;
import java.util.List;

public class TargetElementVisitor extends JavaRecursiveElementWalkingVisitor {
  private List<PsiElement> elementList;
  private PsiElement targetElement;

  public TargetElementVisitor(PsiElement targetElement) {
    elementList = new ArrayList<>();
    this.targetElement = targetElement;
  }

  @Override
  public void visitReferenceExpression(PsiReferenceExpression expression) {
    super.visitReferenceExpression(expression);
    if (expression.getReference() != null) {
      if (targetElement.getText().equals(expression.getReference().resolve().getText())) {
        elementList.add(expression);
      }
    }
  }

  public List<PsiElement> getElementList() {
    return elementList;
  }
}
