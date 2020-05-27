package inspection.codeSmell;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiStatement;
import inspection.CodeSmellInspectionTest;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class MessageChainsInspectionTest extends CodeSmellInspectionTest {
    private final String fileName = "Demo";
    private final String originalFilePath = "MessageChains\\src\\" + fileName + ".java";

    @Override
    public void testForInspection() {
        // 検出基準値の設定はしない
        final MessageChainsInspection inspection = new MessageChainsInspection();

        myFixture.configureByFile(originalFilePath);
        myFixture.enableInspections(inspection);

        // 『メッセージの連鎖』コードインスペクションが検出したコードを特定
        List<HighlightInfo> highlightInfoList = getHighlightInfoList(inspection.getShortName());

        // ハイライト部分の情報を取得
        // 今回のテストケースであれば、検出結果としては1つのみ
        HighlightInfo highlightInfo = highlightInfoList.get(0);
        // ハイライトすべき部分が正しいか確認
        assertEquals(346, highlightInfo.getActualStartOffset());
        assertEquals(388, highlightInfo.getActualEndOffset());
    }

    public void testGetDisplayName() {
        MessageChainsInspection inspection = new MessageChainsInspection();
        assertEquals("Message chains", inspection.getDisplayName());
    }

    public void testGetShortName() {
        MessageChainsInspection inspection = new MessageChainsInspection();
        assertEquals("MessageChainsInspection", inspection.getShortName());
    }

    public void testCreateOptionsPanel() {
        MessageChainsInspection inspection = new MessageChainsInspection();
        JComponent component = inspection.createOptionsPanel();
        JPanel panel = (JPanel) component.getComponent(0);
        JLabel label = (JLabel) panel.getComponent(0);
        String description = "detected length of \"" + inspection.getDisplayName() + "\"";
        assertEquals(description, label.getText());
    }

    public void testCheckExpression() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MessageChainsInspection inspection = new MessageChainsInspection();
        InspectionManager manager = InspectionManager.getInstance(getProject());
        Method method = MessageChainsInspection.class.getDeclaredMethod("checkExpression", PsiExpression.class, InspectionManager.class, boolean.class);
        assert method != null;
        method.setAccessible(true);

        // 上限値以下の時
        PsiStatement statement = getElementFactory().createStatementFromText("System.out.println(\"test\");", null);
        PsiExpression expression = (PsiExpression) statement.getChildren()[0];  // PsiMethodCallExpression
        PsiCodeBlock codeBlock = getElementFactory().createCodeBlock();
        codeBlock.add(expression);
        ProblemDescriptor[] descriptors = (ProblemDescriptor[]) method.invoke(inspection, expression, manager, true);
        assertNull(descriptors);

        // 親のPsiElementがPsiExpression、もしくは継承したものの時
        expression = (PsiExpression) statement.getChildren()[0];  // PsiReferenceExpression
        descriptors = (ProblemDescriptor[]) method.invoke(inspection, expression, manager, true);
        assertNull(descriptors);
    }

    public void testBuildVisitor() {
        /* testForInspectionにて使用しているのでテストはスルー */
    }
}