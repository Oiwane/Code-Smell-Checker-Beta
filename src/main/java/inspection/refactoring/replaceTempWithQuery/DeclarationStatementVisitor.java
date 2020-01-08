package inspection.refactoring.replaceTempWithQuery;

import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;

import java.util.ArrayList;
import java.util.List;

public class DeclarationStatementVisitor extends JavaRecursiveElementWalkingVisitor {
  private List<PsiDeclarationStatement> declarationStatementList;

  public DeclarationStatementVisitor() {
    declarationStatementList = new ArrayList<>();
  }

  @Override
  public void visitDeclarationStatement(PsiDeclarationStatement statement) {
    super.visitDeclarationStatement(statement);

    if (statement.getDeclaredElements().length != 1) return;
    if (ReferencesSearch.search(statement.getDeclaredElements()[0]).toArray(new PsiReference[0]).length == 1) {
      declarationStatementList.add(statement);
    }
  }

  public List<PsiDeclarationStatement> getDeclarationStatementList() {
    return declarationStatementList;
  }
}
