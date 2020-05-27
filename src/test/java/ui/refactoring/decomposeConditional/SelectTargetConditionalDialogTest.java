package ui.refactoring.decomposeConditional;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import ui.refactoring.replaceTempWithQuery.SelectTargetTempDialog;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class SelectTargetConditionalDialogTest extends LightJavaCodeInsightFixtureTestCase {

    public void testDialogTitle() {
        SelectTargetConditionalDialog dialog = new SelectTargetConditionalDialog(getProject(), true, new ArrayList<>());
        assertEquals("Select Conditionals", dialog.getTitle());
    }
}