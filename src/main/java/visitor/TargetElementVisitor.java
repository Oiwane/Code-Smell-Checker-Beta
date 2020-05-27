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
        assert expression.getReference() != null;
        PsiElement resolve = expression.getReference().resolve();
        if (resolve != null && targetElement.getText().equals(resolve.getText())) {
            elementList.add(expression);
        }
    }

    public List<PsiElement> getElementList() {
        return elementList;
    }
}
