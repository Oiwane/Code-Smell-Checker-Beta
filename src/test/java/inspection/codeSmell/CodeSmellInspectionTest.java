package inspection.codeSmell;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

import java.util.ArrayList;
import java.util.List;

class CodeSmellInspectionTest extends LightJavaCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/resources";
    }

    /**
     * コードインスペクションが検出したコードを特定
     *
     * @param inspectionShortName コードインスペクションのID
     * @return コードインスペクションの検出結果
     */
    protected List<HighlightInfo> getHighlightInfoList(final String inspectionShortName) {
        List<HighlightInfo> highlightInfoList = myFixture.doHighlighting();
        List<HighlightInfo> returnValue = new ArrayList<>();

        for (HighlightInfo highlightInfo : highlightInfoList) {
            if (inspectionShortName.equals(highlightInfo.getInspectionToolId())) returnValue.add(highlightInfo);
        }
        assertTrue(returnValue.size() > 0);

        return returnValue;
    }
}
