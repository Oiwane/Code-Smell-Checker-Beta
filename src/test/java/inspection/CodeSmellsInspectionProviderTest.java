package inspection;

import inspection.codeSmell.LongMethodInspection;
import inspection.codeSmell.LongParameterListInspection;
import inspection.codeSmell.MessageChainsInspection;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CodeSmellsInspectionProviderTest {

    @Test
    public void getInspectionClasses() {
        Class[] expected = {
                LongMethodInspection.class,
                LongParameterListInspection.class,
                MessageChainsInspection.class
        };
        CodeSmellsInspectionProvider provider = new CodeSmellsInspectionProvider();
        Class[] classes = provider.getInspectionClasses();

        assertEquals(expected.length, classes.length);
        for (int i = 0; i < classes.length; i++) {
            assertEquals(expected[i], classes[i]);
        }
    }
}