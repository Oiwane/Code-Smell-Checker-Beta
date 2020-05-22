package visitor;

import com.intellij.psi.JavaRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;

import java.util.ArrayList;
import java.util.List;

public class LocalVariableVisitor extends JavaRecursiveElementWalkingVisitor {
    private List<PsiElement> localVariableList;

    public LocalVariableVisitor() {
        localVariableList = new ArrayList<>();
    }

    @Override
    public void visitLocalVariable(PsiLocalVariable variable) {
        super.visitLocalVariable(variable);
        localVariableList.add(variable);
    }

    public List<PsiElement> getLocalVariableList() {
        return localVariableList;
    }
}
