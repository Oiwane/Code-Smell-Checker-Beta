package visitor;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

import java.util.List;

public class LocalVariableVisitorTest extends LightJavaCodeInsightFixtureTestCase {

    public void testVisitLocalVariable() {
        myFixture.addClass(
                "public class LocalVariableVisitorDemo {\n" +
                        "    private static field;\n" +
                        "    \n" +
                        "    public static void main(String[] args) {\n" +
                        "        int local = 0;\n" +
                        "        String str = \"hoge\";\n" +
                        "        System.out.println(\"local : \" + local);\n" +
                        "        System.out.println(\"str : \" + str);\n" +
                        "        System.out.println(\"field : \" + field);\n" +
                        "    }\n" +
                        "}"
        );
        String[] ans = {
                "int local = 0;",
                "String str = \"hoge\";"
        };
        LocalVariableVisitor visitor = new LocalVariableVisitor();
        PsiClass psiClass = myFixture.findClass("LocalVariableVisitorDemo");
        psiClass.accept(visitor);
        List<PsiElement> localVariableList = visitor.getLocalVariableList();
        assertEquals(2, localVariableList.size());
        for (int i = 0; i < localVariableList.size(); i++) {
            assertEquals(ans[i], localVariableList.get(i).getText());
        }
    }

    public void testGetLocalVariableList() {
        // getterは上記のテストで確認
    }
}