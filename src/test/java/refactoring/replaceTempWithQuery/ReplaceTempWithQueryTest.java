package refactoring.replaceTempWithQuery;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReplaceTempWithQueryTest extends LightJavaCodeInsightFixtureTestCase {

    public void testGetFamilyName() {
        ReplaceTempWithQuery replaceTempWithQuery = new ReplaceTempWithQuery();
        assertEquals("Replace Temp with Query", replaceTempWithQuery.getFamilyName());
    }
}