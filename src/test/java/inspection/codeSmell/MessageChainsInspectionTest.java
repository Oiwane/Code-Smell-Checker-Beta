package inspection.codeSmell;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.HighlightTestInfo;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import refactoring.hideDelegate.HideDelegate;

import java.util.List;

import static org.junit.Assert.*;

public class MessageChainsInspectionTest extends CodeSmellInspectionTest implements HighlightTest {
    private final String fileName = "Demo";
    private final String originalFilePath = "MessageChains\\src\\" + fileName + ".java";

    @Override
    public void testForInspection() {
        // 検出基準値の設定はしない
        final MessageChainsInspection messageChainsInspection = new MessageChainsInspection();

        myFixture.configureByFile(originalFilePath);
        myFixture.enableInspections(messageChainsInspection);

        // 『メッセージの連鎖』コードインスペクションが検出したコードを特定
        List<HighlightInfo> highlightInfoList = getHighlightInfoList(messageChainsInspection.getShortName());

        // ハイライト部分の情報を取得
        // 今回のテストケースであれば、検出結果としては1つのみ
        HighlightInfo highlightInfo = highlightInfoList.get(0);
        // ハイライトすべき部分が正しいか確認
        assertEquals(316, highlightInfo.getActualStartOffset());
        assertEquals(358, highlightInfo.getActualEndOffset());
    }
}