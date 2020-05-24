package psi;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiType;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

import java.util.ArrayList;
import java.util.List;

public class PsiUtilTest extends LightJavaCodeInsightFixtureTestCase {

    public void testCloneMethod() {
        // サンプルメソッド
        PsiElementFactory factory = PsiElementFactory.getInstance(getProject());
        PsiMethod method1 = factory.createMethod("test", PsiType.INT);
        PsiParameterList parameterList = method1.getParameterList();
        String[] names = {"num"};
        PsiType[] types = {PsiType.INT};
        parameterList.replace(factory.createParameterList(names, types));
        PsiCodeBlock codeBlock = method1.getBody();
        PsiStatement statement = factory.createStatementFromText("System.out.println(num);", null);
        assert codeBlock != null;
        codeBlock.add(statement);

        PsiMethod method2 = PsiUtil.cloneMethod(method1);
        assertEquals(method1.getNameIdentifier().getText(), method2.getNameIdentifier().getText());
        assertEquals(method1.getParameterList().getText(), method2.getParameterList().getText());
        assertNotSame(method1, method2);
    }

    public void testCloneConstructor() {
        PsiElementFactory factory = PsiElementFactory.getInstance(getProject());
        PsiMethod method1 = factory.createConstructor("Test");
        PsiCodeBlock codeBlock = method1.getBody();
        PsiStatement statement = factory.createStatementFromText("System.out.println(\"test\");", null);
        assert codeBlock != null;
        codeBlock.add(statement);

        PsiMethod method2 = PsiUtil.cloneMethod(method1);
        assertEquals(method1.getNameIdentifier().getText(), method2.getNameIdentifier().getText());
        assertEquals(method1.getParameterList().getText(), method2.getParameterList().getText());
        assertNotSame(method1, method2);
    }

    public void testClonePsiExpression() {
        PsiElementFactory factory = PsiElementFactory.getInstance(getProject());
        PsiExpression expression1 = factory.createExpressionFromText("System.out.println(\"test\")", null);
        PsiExpression expression2 = PsiUtil.clonePsiExpression(expression1);
        assertEquals(expression1.getText(), expression2.getText());
        assertNotSame(expression1, expression2);
    }

    public void testClonePsiParameterList() {
        PsiElementFactory factory = PsiElementFactory.getInstance(getProject());
        String[] names = {"num", "c"};
        PsiType[] types = {PsiType.INT, PsiType.CHAR};
        PsiParameterList parameterList1 = factory.createParameterList(names, types);
        PsiParameterList parameterList2 = PsiUtil.clonePsiParameterList(parameterList1);
        assertEquals(parameterList1.getText(), parameterList2.getText());
        assertNotSame(parameterList1, parameterList2);
    }

    public void testExistsSameMethod() {
        String[][] names = {
                {"num", "c"}, {"num"},
                {"c", "num"}, {"num", "c"}};
        PsiType[][] types = {
                {PsiType.INT, PsiType.CHAR}, {PsiType.INT},
                {PsiType.CHAR, PsiType.INT}, {PsiType.INT, PsiType.CHAR}};
        PsiMethod target = this.createMethod("sameName", names[names.length - 1], types[types.length - 1]);

        List<PsiMethod> samples = new ArrayList<>();
        for (int i = 0; i < names.length - 1; i++) {
            String methodName = i == 0 ? "differenceName" : "sameName";
            PsiMethod method = this.createMethod(methodName, names[i], types[i]);
            samples.add(method);
        }
        assertFalse(PsiUtil.existsSameMethod(target, samples.toArray(new PsiMethod[0])));

        samples.add(this.createMethod("sameName", names[names.length - 1], types[types.length - 1]));
        assertTrue(PsiUtil.existsSameMethod(target, samples.toArray(new PsiMethod[0])));
    }

    private PsiMethod createMethod(String methodName, String[] parameterNames, PsiType[] parameterTypes) {
        PsiElementFactory factory = PsiElementFactory.getInstance(getProject());
        PsiMethod method = factory.createMethod(methodName, PsiType.VOID);
        PsiParameterList parameterList = factory.createParameterList(parameterNames, parameterTypes);
        method.getParameterList().replace(parameterList);
        return method;
    }

    public void testDeleteUnusedMethod() {
        myFixture.addClass(
                "public class PsiUtilDemo {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        test();\n" +
                        "    }\n" +
                        "\n" +
                        "    void test() {\n" +
                        "        System.out.println(\"test\");\n" +
                        "    }\n" +
                        "\n" +
                        "    void test(int num) {\n" +
                        "        System.out.println(\"num : \" + num);\n" +
                        "    }\n" +
                        "}"
        );
        PsiClass psiClass = myFixture.findClass("PsiUtilDemo");
        int methodNum = psiClass.getAllMethods().length;
        WriteCommandAction.runWriteCommandAction(getProject(), () -> {
            PsiUtil.deleteUnusedMethod(psiClass, "test");
            assertEquals(methodNum - 1, psiClass.getAllMethods().length);
        });
    }

    public void testFindBaseElement() {
        PsiElementFactory factory = PsiElementFactory.getInstance(getProject());
        PsiExpression expression = factory.createExpressionFromText("str.substring(0,4).substring(0,3)", null);
        PsiReferenceExpression referenceExpression = PsiUtil.findBaseElement(expression);
        assertEquals("str", referenceExpression.getText());
    }
}