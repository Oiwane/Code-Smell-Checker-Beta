package ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

/**
 * コードスメル表示部を表示するためのクラス
 */
public class RefactoringNavigatorToolWindowFactory implements ToolWindowFactory {

  /**
   * コードスメル表示部を作成する
   * @param project [プロジェクト]
   * @param toolWindow [ツールウィンドウ]
   */
  @Override
  public void createToolWindowContent(Project project, ToolWindow toolWindow) {
    RefactoringNavigatorToolWindow window = new RefactoringNavigatorToolWindow(project);
    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(window.getContent(), "", false);
    toolWindow.getContentManager().addContent(content);
  }
}
