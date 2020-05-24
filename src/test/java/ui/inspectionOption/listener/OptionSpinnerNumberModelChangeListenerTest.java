package ui.inspectionOption.listener;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import inspection.InspectionData;

import javax.swing.JButton;
import javax.swing.JTextField;

public class OptionSpinnerNumberModelChangeListenerTest extends LightJavaCodeInsightFixtureTestCase {

    public void testStateChanged() {
        JButton button = new JButton();
        JTextField textField = new JTextField();
        InspectionData inspectionData = InspectionData.getInstance(InspectionData.InspectionDataKey.LONG_METHOD);
        OptionSpinnerNumberModelChangeListener listener = new OptionSpinnerNumberModelChangeListener(button, textField, inspectionData);

        // 処理内容がPsiUtil.changeAvailabilityButtonを呼び出すだけなので特にテストはなし
        listener.stateChanged(null);
    }
}