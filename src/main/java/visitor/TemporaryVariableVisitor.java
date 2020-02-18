package visitor;

import com.intellij.psi.JavaRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;

import java.util.ArrayList;
import java.util.List;

public class TemporaryVariableVisitor extends JavaRecursiveElementWalkingVisitor {
  private List<PsiElement> tempVariableList;

  public TemporaryVariableVisitor() {
    tempVariableList = new ArrayList<>();
  }

  @Override
  public void visitLocalVariable(PsiLocalVariable variable) {
    super.visitLocalVariable(variable);

    if (ReferencesSearch.search(variable).toArray(new PsiReference[0]).length == 1) {
      tempVariableList.add(variable);
    }
  }

  public List<PsiElement> getTempVariableList() {
    return tempVariableList;
  }
}
