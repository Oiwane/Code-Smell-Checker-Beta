package visitor;

import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiPolyadicExpression;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

public class ConditionalVisitorTest extends LightJavaCodeInsightFixtureTestCase {

    public void testVisitPolyadicExpression() {
        PsiExpression expression1 = getElementFactory().createExpressionFromText("elementType.equals(JavaTokenType.OROR) || elementType.equals(JavaTokenType.ANDAND)", null);
        PsiExpression expression2 = getElementFactory().createExpressionFromText("a == b", null);

        ConditionalVisitor visitor = new ConditionalVisitor();
        visitor.visitPolyadicExpression((PsiPolyadicExpression) expression1);
        visitor.visitPolyadicExpression((PsiPolyadicExpression) expression2);
        assertEquals(1, visitor.getConditionalList().size());
        assertEquals(
                "elementType.equals(JavaTokenType.OROR) || elementType.equals(JavaTokenType.ANDAND)",
                visitor.getConditionalList().get(0).getText()
        );
    }

    public void testGetConditionalList() {
        // getterは上記のテストで確認
    }
}