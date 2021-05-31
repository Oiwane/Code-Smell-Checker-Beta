package ui.refactoring.replaceTempWithQuery;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

import java.util.ArrayList;

public class SelectTargetTempDialogTest extends LightJavaCodeInsightFixtureTestCase {

    public void testDialogTitle() {
        SelectTargetTempDialog dialog = new SelectTargetTempDialog(getProject(), true, new ArrayList<>(), true);
        assertEquals("Select Temps", dialog.getTitle());
    }
}