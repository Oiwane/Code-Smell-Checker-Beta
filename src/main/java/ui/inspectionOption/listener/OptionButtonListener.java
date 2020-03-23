package ui.inspectionOption.listener;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import ui.inspectionOption.InspectionOptionUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * UI上のボタンを押したかを監視するリスナークラス
 */
public class OptionButtonListener implements ActionListener {
  private JTextField myTextField;
  private final String myPropertiesComponentName;

  public OptionButtonListener(JTextField textField, String propertiesComponentName) {
    myTextField = textField;
    myPropertiesComponentName = propertiesComponentName;
  }

  public void actionPerformed(ActionEvent e) {
    String value = myTextField.getText();
    if (Integer.parseInt(value) >= InspectionOptionUtil.LIMIT_MIN_VALUE) {
      PropertiesComponent.getInstance().setValue(myPropertiesComponentName, value);
      final Project project = ProjectUtil.guessCurrentProject(myTextField);
      DaemonCodeAnalyzer.getInstance(project).restart();
    }
  }

}
