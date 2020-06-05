package refactoring.introduceParameterObject;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

public class IntroduceParameterObjectTest extends LightJavaCodeInsightFixtureTestCase {

    public void testGetFamilyName() {
        IntroduceParameterObject introduceParameterObject = new IntroduceParameterObject();
        assertEquals("Introduce Parameter Object", introduceParameterObject.getFamilyName());
    }
}