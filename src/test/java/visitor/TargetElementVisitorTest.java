package visitor;

import com.intellij.psi.*;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

import java.util.List;

public class TargetElementVisitorTest extends LightJavaCodeInsightFixtureTestCase {

    public void testVisitReferenceExpression() {
        myFixture.addClass(
        "public class TargetElementVisitorDemo {\n" +
                "    public void test(String str) {\n" +
                "        int num = 0;\n" +
                "        System.out.println(str);\n" +
                "        System.out.println(num);\n" +
                "    }\n" +
                "}"
        );
        PsiClass psiClass = myFixture.findClass("TargetElementVisitorDemo");
        PsiMethod method = psiClass.findMethodsByName("test", false)[0];
        PsiElement target = method.getParameterList().getParameters()[0];   // address
        TargetElementVisitor visitor = new TargetElementVisitor(target);
        method.accept(visitor);
        List<PsiElement> elementList = visitor.getElementList();
        assertEquals(1, elementList.size());
        assertEquals("str", elementList.get(0).getText());
    }

    public void testGetElementList() {
        // getterは上記のテストで確認
    }
}