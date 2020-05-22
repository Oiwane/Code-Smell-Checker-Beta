package inspection.codeSmell;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.ide.util.PropertiesComponent;
import inspection.InspectionSettingName;

import java.util.List;

public class LongMethodInspectionTest extends CodeSmellInspectionTest implements HighlightTest {
    private final String fileName = "Item";
    private final String originalFilePath = "LongMethod\\src\\item\\" + fileName + ".java";

    public void testForInspection() {
        final LongMethodInspection longMethodInspection = new LongMethodInspection();
        // 検出基準値を15に設定
        PropertiesComponent.getInstance().setValue(InspectionSettingName.LONG_METHOD_PROPERTIES_COMPONENT_NAME.getName(), "15");

        myFixture.configureByFile(originalFilePath);
        myFixture.enableInspections(longMethodInspection);

        // 『長すぎるメソッド』コードインスペクションが検出したコードを特定
        List<HighlightInfo> highlightInfoList = getHighlightInfoList(longMethodInspection.getShortName());

        // ハイライト部分の情報を取得
        // 今回のテストケースであれば、検出結果としては1つのみ
        HighlightInfo info = highlightInfoList.get(0);
        // ハイライトすべき部分が正しいか確認
        assertEquals(689, info.getActualStartOffset());
        assertEquals(693, info.getActualEndOffset());
    }
}
