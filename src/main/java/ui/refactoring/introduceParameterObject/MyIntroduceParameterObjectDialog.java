package ui.refactoring.introduceParameterObject;

import com.intellij.psi.PsiMethod;
import com.intellij.refactoring.introduceparameterobject.IntroduceParameterObjectDialog;

public class MyIntroduceParameterObjectDialog {
    private final IntroduceParameterObjectDialog dialog;
    private final boolean isTest;

    public MyIntroduceParameterObjectDialog(PsiMethod sourceMethod, boolean isTest) {
        dialog = new IntroduceParameterObjectDialog(sourceMethod);
        this.isTest = isTest;
    }

    public void show() {
        if (!isTest)
            dialog.show();
    }
}
