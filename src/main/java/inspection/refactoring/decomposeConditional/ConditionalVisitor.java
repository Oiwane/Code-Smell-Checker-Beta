package inspection.refactoring.decomposeConditional;

import com.intellij.psi.JavaRecursiveElementWalkingVisitor;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyadicExpression;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConditionalVisitor extends JavaRecursiveElementWalkingVisitor {
  private List<PsiElement> conditionalList;

  ConditionalVisitor() {
    conditionalList = new ArrayList<>();
  }

  @Override
  public void visitPolyadicExpression(PsiPolyadicExpression expression) {
    super.visitPolyadicExpression(expression);

    IElementType elementType = expression.getOperationTokenType();
    if (hasMultipleConditionalExpression(elementType)) conditionalList.add(expression);
  }

  List<PsiElement> getConditionalList() {
    return conditionalList;
  }

  private boolean hasMultipleConditionalExpression(@NotNull IElementType elementType) {
    return elementType.equals(JavaTokenType.OROR) || elementType.equals(JavaTokenType.ANDAND);
  }

}
