package refactoring.introduceParameterObject;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import inspection.codeSmell.LongParameterListInspection;
import org.junit.Before;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class IntroduceParameterObjectTest extends LightJavaCodeInsightFixtureTestCase {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        File file = new File("src/test/resources/LongParameterList/src/Main.java");
        BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
        StringBuilder builder = new StringBuilder();

        String line = reader.readLine();
        while(line != null) {
            builder.append(line);
            builder.append("\n");
            line = reader.readLine();
        }

        myFixture.addClass(builder.toString());
    }

    public void testGetFamilyName() {
        IntroduceParameterObject introduceParameterObject = new IntroduceParameterObject();
        assertEquals("Introduce Parameter Object", introduceParameterObject.getFamilyName());
    }

    // カバレッジのため
    public void testApplyFix() {
        PsiClass psiClass = myFixture.findClass("Main");
        PsiMethod psiMethod = psiClass.findMethodsByName("introduceOneself", true)[0];
        try {
            Method method = LongParameterListInspection.class.getDeclaredMethod(
                    "checkParameterList", PsiParameterList.class, InspectionManager.class, boolean.class);
            method.setAccessible(true);
            LongParameterListInspection inspection = new LongParameterListInspection();
            ProblemDescriptor[] descriptors = (ProblemDescriptor[]) method.invoke(
                    inspection, psiMethod.getParameterList(), InspectionManager.getInstance(getProject()), true);
            LocalQuickFix quickFix = new IntroduceParameterObject(true);
            quickFix.applyFix(getProject(), descriptors[0]);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}