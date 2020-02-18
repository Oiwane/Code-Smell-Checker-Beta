package ui.refactoring.extractMethod;

import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.EditorTextField;

public class MyEditorTextField extends EditorTextField {

  MyEditorTextField(String text, Project project, FileType fileType) {
    super(text, project, fileType);
  }

  @Override
  protected EditorEx createEditor() {
    EditorEx editorEx = super.createEditor();
    editorEx.setVerticalScrollbarVisible(true);

    return editorEx;
  }

}
