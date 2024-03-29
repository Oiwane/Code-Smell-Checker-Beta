package inspection;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

import java.util.ArrayList;
import java.util.List;

public class CodeSmellInspectionTest extends LightJavaCodeInsightFixtureTestCase {
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
            if (inspectionShortName.equals(highlightInfo.getInspectionToolId())) {
                returnValue.add(highlightInfo);
            }
        }
        assertTrue(returnValue.size() > 0);

        return returnValue;
    }

    /**
     * コードインスペクションのハイライト表示テスト
     *
     * このメソッドにコードインスペクションのハイライト表示のテストコードを書く
     */
    public void testForInspection() {
        // 特にやることなし
    }

    public void testCreateOptionUI() {
        // 特にやることなし
        CodeSmellInspection inspection = new ConcreteCodeSmellInspection();
        InspectionData inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.LONG_METHOD);
        inspection.createOptionUI("test", inspectionData);
    }

    public void testGetGroupDisplayName() {
        CodeSmellInspection inspection = new ConcreteCodeSmellInspection();
        assertEquals("Code smell", inspection.getGroupDisplayName());
    }

    public void testIsEnabledByDefault() {
        CodeSmellInspection inspection = new ConcreteCodeSmellInspection();
        assertFalse(inspection.isEnabledByDefault());
    }

    public void testGetDefaultLevel() {
        CodeSmellInspection inspection = new ConcreteCodeSmellInspection();
        assertEquals(HighlightDisplayLevel.WARNING, inspection.getDefaultLevel());
    }

    public void testSetAndGetInspectionData() {
        InspectionData inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.NULL);
        CodeSmellInspection inspection = new ConcreteCodeSmellInspection();
        inspection.setInspectionData(inspectionData);
        assertEquals(inspectionData, inspection.getInspectionData());
    }

    public void testGetUpperLimitValue() {
        InspectionData inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.LONG_PARAMETER_LIST);
        CodeSmellInspection inspection = new ConcreteCodeSmellInspection();
        inspection.setInspectionData(inspectionData);
        assertEquals(inspectionData.getUpperLimitValue(), inspection.getUpperLimitValue());
    }
}
