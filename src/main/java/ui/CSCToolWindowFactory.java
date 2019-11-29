package ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * コードスメル表示部を表示するためのクラス
 */
public class CSCToolWindowFactory implements ToolWindowFactory {

  /**
   * コードスメル表示部を作成する
   * @param project [プロジェクト]
   * @param toolWindow [ツールウィンドウ]
   */
  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    CSCToolWindow window = new CSCToolWindow(project);
    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(window.getContent(), null, false);
    toolWindow.getContentManager().addContent(content);
  }
}
