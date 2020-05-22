package inspection.codeSmell;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.ide.util.PropertiesComponent;
import inspection.CodeSmellInspectionTest;
import inspection.InspectionData;

import java.util.List;

public class LongMethodInspectionTest extends CodeSmellInspectionTest implements HighlightTest {
    private final String fileName = "Item";
    private final String originalFilePath = "LongMethod\\src\\item\\" + fileName + ".java";

    public void testForInspection() {
        final LongMethodInspection longMethodInspection = new LongMethodInspection();
        // 検出基準値を15に設定
        PropertiesComponent.getInstance().setValue(InspectionData.LONG_METHOD_PROPERTIES_COMPONENT_NAME, "15");

        myFixture.configureByFile(originalFilePath);
        myFixture.enableInspections(longMethodInspection);

        // 『長すぎるメソッド』コードインスペクションが検出したコードを特定
        List<HighlightInfo> highlightInfoList = getHighlightInfoList(longMethodInspection.getShortName());

        // ハイライト部分の情報を取得
        // 今回のテストケースであれば、検出結果としては1つのみ
        HighlightInfo info = highlightInfoList.get(0);
        // ハイライトすべき部分が正しいか確認
        assertEquals(753, info.getActualStartOffset());
        assertEquals(757, info.getActualEndOffset());
    }
}
