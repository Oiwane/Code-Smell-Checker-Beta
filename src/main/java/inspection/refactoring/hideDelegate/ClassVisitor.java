package inspection.refactoring.hideDelegate;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiClass;

public class ClassVisitor extends JavaRecursiveElementWalkingVisitor {
  private PsiClass targetClass;
  private TextRange targetElementRange;

  public ClassVisitor(TextRange targetElementRange) {
    this.targetElementRange = targetElementRange;
  }

  @Override
  public void visitClass(PsiClass aClass) {
    super.visitClass(aClass);

    if (aClass.getTextRange().contains(targetElementRange)) targetClass = aClass;
  }

  public PsiClass getTargetClass() {
    return targetClass;
  }
}
