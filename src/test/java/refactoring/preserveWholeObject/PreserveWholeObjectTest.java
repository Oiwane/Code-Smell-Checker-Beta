package refactoring.preserveWholeObject;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

public class PreserveWholeObjectTest extends LightJavaCodeInsightFixtureTestCase {

    public void testGetFamilyName() {
        PreserveWholeObject preserveWholeObject = new PreserveWholeObject();
        assertEquals("Preserve Whole Object", preserveWholeObject.getFamilyName());
    }
}