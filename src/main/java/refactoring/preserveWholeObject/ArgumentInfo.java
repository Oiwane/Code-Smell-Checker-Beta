package refactoring.preserveWholeObject;

import com.intellij.psi.PsiMethodCallExpression;

public class ArgumentInfo {
  private int index;
  private PsiMethodCallExpression argumentMethod;

  public ArgumentInfo(int index, PsiMethodCallExpression argumentMethod) {
    this.index = index;
    this.argumentMethod = argumentMethod;
  }

  public int getIndex() {
    return index;
  }

  public PsiMethodCallExpression getArgumentMethod() {
    return argumentMethod;
  }
}
