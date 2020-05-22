package inspection.codeSmell;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import inspection.CodeSmellInspectionTest;
import inspection.InspectionData;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import java.awt.Component;
import java.util.List;

public class LongMethodInspectionTest extends CodeSmellInspectionTest {
    private final String fileName = "Item";
    private final String originalFilePath = "LongMethod\\src\\item\\" + fileName + ".java";

    @Override
    public void testForInspection() {
        final LongMethodInspection longMethodInspection = new LongMethodInspection();
        InspectionData inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.LONG_METHOD);
        assert inspectionData != null;
        // 検出基準値を15に設定
        PropertiesComponent.getInstance().setValue(inspectionData.getComponentName(), "15");

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

    public void testGetDisplayName() {
        LongMethodInspection inspection = new LongMethodInspection();
        assertEquals("Long method", inspection.getDisplayName());
    }

    public void testGetShortName() {
        LongMethodInspection inspection = new LongMethodInspection();
        assertEquals("LongMethodInspection", inspection.getShortName());
    }

    public void testCreateOptionsPanel() {
        LongMethodInspection inspection = new LongMethodInspection();
        JComponent component = inspection.createOptionsPanel();
        Class[] classes = {JPanel.class,    // spinnerPanel
                JLabel.class,               // descriptionLable
                JSpinner.class,             // spinner
                JPanel.class,               // buttonPanel
                JButton.class};             // button
        int index = 0;
        for (Component child : component.getComponents()) {
            assertEquals(classes[index++], child.getClass());
            for (Component grandChild : ((JComponent) child).getComponents()) {
                assertEquals(classes[index++], grandChild.getClass());
            }
        }
    }

    public void testCheckMethod() {
        LongMethodInspection inspection = new LongMethodInspection();
        PsiElementFactory factory = PsiElementFactory.getInstance(getProject());

        // メソッドのボディがnullの時
        PsiMethod target = factory.createMethodFromText("public void test()", null);
        InspectionManager manager = InspectionManager.getInstance(getProject());
        ProblemDescriptor[] descriptors = inspection.checkMethod(target, manager, true);
        assertNull(descriptors);

        // 検出対象でなかった時
        target = factory.createMethod("test", PsiType.VOID, null);
        descriptors = inspection.checkMethod(target, manager, true);
        assertNull(descriptors);

        // メソッド名が無い時
        InspectionData inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.LONG_METHOD);
        assert inspectionData != null;
        PropertiesComponent.getInstance().setValue(inspectionData.getComponentName(), "1");
        target = factory.createMethodFromText("public void (){System.out.print();System.out.print();}", null);
        descriptors = inspection.checkMethod(target, manager, true);
        assertNull(descriptors);

        /* testForInspectionにて使用しているので検出時のテストはスルー */
    }

    public void testBuildVisitor() {
        /* testForInspectionにて使用しているのでテストはスルー */
    }
}
