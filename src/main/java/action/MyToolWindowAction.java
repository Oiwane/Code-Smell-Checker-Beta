package action;

import window.MyToolWindow;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

public class MyToolWindowAction extends AnAction {

    public MyToolWindowAction() {

    }

    @NotNull
    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        Project project = actionEvent.getProject();

        this.resetContent(project);
    }

    private void resetContent(Project project) {
        MyToolWindow toolWindow = new MyToolWindow(project);
        ContentManager contentManager = (ContentManager) ToolWindowManager.getInstance(project).getToolWindow("Refactoring for Java");
        Content content = contentManager.getFactory().createContent(toolWindow, null, false);

        contentManager.removeAllContents(true);
        contentManager.addContent(content);
    }
}
