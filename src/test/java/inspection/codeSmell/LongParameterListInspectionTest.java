package inspection.codeSmell;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import inspection.CodeSmellInspectionTest;

<<<<<<< HEAD
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
=======
import javax.swing.*;
>>>>>>> 2afcc03ead84059474ce70edb5a3a4e5c6e146bf
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class LongParameterListInspectionTest extends CodeSmellInspectionTest {
    private final String fileName = "Main";
    private final String originalFilePath = "LongParameterList\\src\\" + fileName + ".java";

    @Override
    public void testForInspection() {
        final LongParameterListInspection inspection = new LongParameterListInspection();

        myFixture.configureByFile(originalFilePath);
        myFixture.enableInspections(inspection);

        List<HighlightInfo> highlightInfoList = getHighlightInfoList(inspection.getShortName());

        HighlightInfo highlightInfo = highlightInfoList.get(0);

        assertEquals(573, highlightInfo.getActualStartOffset());
        assertEquals(686, highlightInfo.getActualEndOffset());
    }

    public void testGetDisplayName() {
        LongParameterListInspection inspection = new LongParameterListInspection();
        assertEquals("Long parameter list", inspection.getDisplayName());
    }

    public void testGetShortName() {
        LongParameterListInspection inspection = new LongParameterListInspection();
        assertEquals("LongParameterListInspection", inspection.getShortName());
    }

    public void testCreateOptionsPanel() {
        LongParameterListInspection inspection = new LongParameterListInspection();
        JComponent component = inspection.createOptionsPanel();
        JPanel panel = (JPanel) component.getComponent(0);
        JLabel label = (JLabel) panel.getComponent(0);
        String description = "detected length of \"" + inspection.getDisplayName() + "\"";
        assertEquals(description, label.getText());
    }

    public void testCheckParameterList() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LongParameterListInspection inspection = new LongParameterListInspection();
        InspectionManager manager = InspectionManager.getInstance(getProject());
        Method method = LongParameterListInspection.class.getDeclaredMethod("checkParameterList", PsiParameterList.class, InspectionManager.class, boolean.class);
        assert method != null;
        method.setAccessible(true);

        PsiElementFactory factory = PsiElementFactory.getInstance(getProject());
        String[] names = {""};
        PsiType[] types = {PsiType.VOID};
        PsiParameterList parameterList = factory.createParameterList(names, types);

        ProblemDescriptor[] descriptors = (ProblemDescriptor[]) method.invoke(inspection, parameterList, manager, true);
        assertNull(descriptors);
    }

    public void testBuildVisitor() {
        /* testForInspectionにて使用しているのでテストはスルー */
    }
}