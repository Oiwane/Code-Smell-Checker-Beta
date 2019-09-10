package action;

import com.intellij.openapi.actionSystem.*;
import ui.MyToolWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class MyToolWindowAction extends AnAction {

  public MyToolWindowAction() {

  }

  @NotNull
  @Override
  public void actionPerformed(AnActionEvent actionEvent) {
    Project project = actionEvent.getData(PlatformDataKeys.PROJECT);

    this.resetContent(project);
  }

  private void resetContent(Project project) {
    MyToolWindow toolWindow = new MyToolWindow(project);
    ContentManager contentManager = (ContentManager) ToolWindowManager.getInstance(project).getToolWindow("Refactoring Navigator");
    Content content = contentManager.getFactory().createContent(toolWindow, null, false);

    contentManager.removeAllContents(true);
    contentManager.addContent(content);
  }
}
