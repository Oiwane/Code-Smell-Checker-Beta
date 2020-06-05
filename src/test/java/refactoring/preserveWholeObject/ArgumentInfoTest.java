package refactoring.preserveWholeObject;

import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

public class ArgumentInfoTest extends LightJavaCodeInsightFixtureTestCase {

    public void testGetter() {
        String methodCallStr = "System.out.println()";
        PsiMethodCallExpression expression = (PsiMethodCallExpression) getElementFactory().createExpressionFromText(methodCallStr, null);
        ArgumentInfo argumentInfo = new ArgumentInfo(0, expression);
        assertEquals(0, argumentInfo.getIndex());
        assertEquals(methodCallStr, argumentInfo.getArgumentMethod().getText());
    }
}