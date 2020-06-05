package refactoring.replaceParameterWithMethod;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

public class ReplaceParameterWithMethodTest extends LightJavaCodeInsightFixtureTestCase {

    public void testGetFamilyName() {
        ReplaceParameterWithMethod replaceParameterWithMethod = new ReplaceParameterWithMethod();
        assertEquals("Replace Parameter with Method", replaceParameterWithMethod.getFamilyName());
    }
}