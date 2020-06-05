package refactoring.decomposeConditional;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

public class DecomposeConditionalTest extends LightJavaCodeInsightFixtureTestCase {

    public void testGetFamilyName() {
        DecomposeConditional decomposeConditional = new DecomposeConditional();
        assertEquals("Decompose Conditional", decomposeConditional.getFamilyName());
    }
}