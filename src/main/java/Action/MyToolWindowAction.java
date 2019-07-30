package Action;

import Window.MyToolWindow;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.sun.jna.platform.unix.X11;
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
