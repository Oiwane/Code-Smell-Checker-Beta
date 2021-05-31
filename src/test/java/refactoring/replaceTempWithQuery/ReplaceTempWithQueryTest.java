package refactoring.replaceTempWithQuery;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import inspection.InspectionData;
import inspection.codeSmell.LongMethodInspection;
import org.junit.Before;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ReplaceTempWithQueryTest extends LightJavaCodeInsightFixtureTestCase {

    private PsiClass psiClass;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        File file = new File("src/test/resources/LongMethod/src/item/Item.java");
        BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
        StringBuilder builder = new StringBuilder();

        String line = reader.readLine();
        while(line != null) {
            builder.append(line);
            builder.append("\n");
            line = reader.readLine();
        }

        psiClass = myFixture.addClass(builder.toString());
    }

    public void testGetFamilyName() {
        ReplaceTempWithQuery replaceTempWithQuery = new ReplaceTempWithQuery();
        assertEquals("Replace Temp with Query", replaceTempWithQuery.getFamilyName());
    }

    public void testApplyFix1() {
        applyFix(true);
    }

    public void testApplyFix2() {
        applyFix(false);
    }

    private void applyFix(boolean isOKReturnValue) {
        // 何故かfineClass()が失敗するので、addClass()の戻り値を使う
        //PsiClass psiClass = myFixture.findClass("Item");
        PsiMethod psiMethod = psiClass.findMethodsByName("demo", false)[0];
        InspectionData inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.LONG_METHOD);
        assert inspectionData != null;
        PropertiesComponent.getInstance().setValue(inspectionData.getComponentName(), "10");
        LongMethodInspection inspection = new LongMethodInspection();
        Project project = getProject();
        ProblemDescriptor[] descriptors = inspection.checkMethod(psiMethod, InspectionManager.getInstance(project), true);
        LocalQuickFix quickFix = new ReplaceTempWithQuery(true, isOKReturnValue);
        assert descriptors != null;
        quickFix.applyFix(project, descriptors[0]);
    }
}