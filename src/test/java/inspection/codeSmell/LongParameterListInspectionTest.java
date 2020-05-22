package inspection.codeSmell;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;

import java.util.List;

public class LongParameterListInspectionTest extends CodeSmellInspectionTest implements HighlightTest {
    private final String fileName = "Main";
    private final String originalFilePath = "LongParameterList\\src\\" + fileName + ".java";

    @Override
    public void testForInspection() {
        final LongParameterListInspection longParameterListInspection = new LongParameterListInspection();

        myFixture.configureByFile(originalFilePath);
        myFixture.enableInspections(longParameterListInspection);

        List<HighlightInfo> highlightInfoList = getHighlightInfoList(longParameterListInspection.getShortName());

        HighlightInfo highlightInfo = highlightInfoList.get(0);

        assertEquals(537, highlightInfo.getActualStartOffset());
        assertEquals(650, highlightInfo.getActualEndOffset());
    }
}