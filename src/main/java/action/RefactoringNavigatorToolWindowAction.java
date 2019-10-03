package action;

import com.intellij.openapi.actionSystem.*;
import ui.RefactoringNavigatorToolWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

public class RefactoringNavigatorToolWindowAction extends AnAction {

  public RefactoringNavigatorToolWindowAction() {

  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent actionEvent) {
    Project project = actionEvent.getData(PlatformDataKeys.PROJECT);

    this.resetContent(project);
  }

  private void resetContent(Project project) {
    RefactoringNavigatorToolWindow toolWindow = new RefactoringNavigatorToolWindow(project);
    ContentManager contentManager = (ContentManager) ToolWindowManager.getInstance(project).getToolWindow("Refactoring Navigator");
    Content content = contentManager.getFactory().createContent(toolWindow, null, false);

    contentManager.removeAllContents(true);
    contentManager.addContent(content);
  }
}
