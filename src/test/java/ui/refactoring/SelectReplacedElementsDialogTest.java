package ui.refactoring;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiStatement;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SelectReplacedElementsDialogTest extends LightJavaCodeInsightFixtureTestCase {

    public void testDoOKAction() {
        List<PsiElement> replacedElementList = new ArrayList<>();
        SelectReplacedElementsDialog dialog = new SelectReplacedElementsDialog(getProject(), true, replacedElementList, "test");
        dialog.doOKAction();
        assertEquals(0, dialog.getSelectedIndexList().size());

        PsiElementFactory factory = getElementFactory();
        PsiStatement statement = factory.createStatementFromText("System.out.println(\"hoge\");", null);
        replacedElementList.add(statement);
        dialog = new SelectReplacedElementsDialog(getProject(), true, replacedElementList, "test");
        dialog.doOKAction();
        assertEquals(1, dialog.getSelectedIndexList().size());
    }

    public void testGetSelectedIndexList() {
        SelectReplacedElementsDialog dialog = new SelectReplacedElementsDialog(getProject(), true, new ArrayList<>(), "test");
        assertEquals(0, dialog.getSelectedIndexList().size());
    }

    public void testCreateCenterPanel() {
        List<Class> classList = new ArrayList<>();
        classList.add(JPanel.class);        // no name
        classList.add(JScrollPane.class);   // scrollPane
        classList.add(JPanel.class);        // checkboxPanel
        classList.add(JLabel.class);        // errorLabel

        List<PsiElement> replacedElementList = new ArrayList<>();
        SelectReplacedElementsDialog dialog = new SelectReplacedElementsDialog(getProject(), true, replacedElementList, "test");
        JComponent centerPanel = dialog.createCenterPanel();
        Component[] components = centerPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            assertEquals(classList.get(i), components[i].getClass());
        }

        classList.clear();
        classList.add(JPanel.class);        // no name
        classList.add(JScrollPane.class);   // scrollPane
        classList.add(JViewport.class);     // no name
        classList.add(JPanel.class);        // checkboxPanel
        classList.add(Box.class);           // verticalBox
        classList.add(JCheckBox.class);     // no name

        PsiElementFactory factory = getElementFactory();
        PsiStatement statement = factory.createStatementFromText("System.out.println(\"hoge\");", null);
        replacedElementList.add(statement);
        dialog = new SelectReplacedElementsDialog(getProject(), true, replacedElementList, "test");
        centerPanel = dialog.createCenterPanel();
        components = centerPanel.getComponents();
        int count = 0;
        for (Component component : components) {
            assertEquals(classList.get(count++), component.getClass());
        }
        components = ((JComponent)components[1]).getComponents();
        assertEquals(classList.get(count++), components[0].getClass());
        int num = classList.size() - count;
        for (int i = 0; i < num; i++) {
            components = ((JComponent)components[0]).getComponents();
            assertEquals(classList.get(count++), components[0].getClass());
        }
    }
}