package refactoring;

import com.intellij.psi.*;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Before;

import static org.junit.Assert.assertNotEquals;

public class RefactoringUtilTest extends LightJavaCodeInsightFixtureTestCase {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        myFixture.addClass(
                "public class RefactoringUtilDemo {\n" +
                        "    public boolean test(int num) {\n" +
                        "        boolean isDone;\n" +
                        "        if (num > 0) {\n" +
                        "            isDone = true;\n" +
                        "        } else {\n" +
                        "            isDone = false;\n" +
                        "        }\n" +
                        "        return isDone;\n" +
                        "    }\n" +
                        "\n" +
                        "    public void test(String str) {\n" +
                        "        System.out.println(str);\n" +
                        "        System.out.println(str);\n" +
                        "    }\n" +
                        "\n" +
                        "    public void test(boolean isDone) {" +
                        "    }" +
                        "}"
        );
    }

    public void testReplaceParameterObject() {
        PsiClass psiClass = myFixture.findClass("RefactoringUtilDemo");
        PsiMethod[] methods = psiClass.findMethodsByName("test", false);

        PsiParameter parameter = methods[0].getParameterList().getParameters()[0];
        String methodText = methods[0].getText();
        PsiReferenceExpression newElement = (PsiReferenceExpression) getElementFactory().createExpressionFromText("value", null);
        RefactoringUtil.replaceParameterObject(methods[0], parameter, newElement);
        assertNotEquals(methodText, methods[0].getText());
        assertEquals("value", newElement.getText());

        parameter = methods[1].getParameterList().getParameters()[0];
        methodText = methods[1].getText();
        newElement = (PsiReferenceExpression) getElementFactory().createExpressionFromText("value", null);
        RefactoringUtil.replaceParameterObject(methods[1], parameter, newElement);
        assertNotEquals(methodText, methods[1].getText());
        assertEquals("String str = value;", methods[1].getBody().getStatements()[0].getText());
        assertEquals("value", newElement.getText());

        parameter = methods[2].getParameterList().getParameters()[0];
        methodText = methods[2].getText();
        newElement = (PsiReferenceExpression) getElementFactory().createExpressionFromText("value", null);
        RefactoringUtil.replaceParameterObject(methods[2], parameter, newElement);
        assertEquals(methodText, methods[2].getText());
    }

    public void testFindCodeBlockBelongsTo() {
        PsiClass psiClass = myFixture.findClass("RefactoringUtilDemo");
        PsiMethod method = psiClass.findMethodsByName("test", false)[0];
        PsiIfStatement ifStatement = (PsiIfStatement) method.getBody().getStatements()[1];
        PsiBlockStatement blockStatement = (PsiBlockStatement) ifStatement.getThenBranch();
        PsiCodeBlock codeBlock = blockStatement.getCodeBlock();
        PsiStatement statement = codeBlock.getStatements()[0];
        PsiElement element = statement.getFirstChild();
        assertEquals(codeBlock.getText(), RefactoringUtil.findCodeBlockBelongsTo(element).getText());
    }

    public void testFindMethodBelongsTo() {
        PsiClass psiClass = myFixture.findClass("RefactoringUtilDemo");
        PsiMethod method = psiClass.findMethodsByName("test", false)[0];
        PsiIfStatement ifStatement = (PsiIfStatement) method.getBody().getStatements()[1];
        PsiBlockStatement blockStatement = (PsiBlockStatement) ifStatement.getThenBranch();
        PsiStatement statement = blockStatement.getCodeBlock().getStatements()[0];
        assertEquals(method.getText(), RefactoringUtil.findMethodBelongsTo(statement).getText());
    }
}