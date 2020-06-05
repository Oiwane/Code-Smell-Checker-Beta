package refactoring.hideDelegate;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

public class HideDelegateTest extends LightJavaCodeInsightFixtureTestCase {

    public void testGetFamilyName() {
        HideDelegate hideDelegate = new HideDelegate();
        assertEquals("Hide Delegate", hideDelegate.getFamilyName());
    }

    public void testApplyFix() {
    }
}