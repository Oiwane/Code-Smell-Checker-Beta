package ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import inspection.CodeSmellInspection;
import inspection.longMethod.LongMethodInspection;
import inspection.longParameterList.LongParameterListInspection;
import inspection.messageChains.MessageChainsInspection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CSCToolWindowUtil {
  /**
   * リストにインスペクションを追加する
   *
   * @param inspectionTools [自作インスペクションのリスト]
   */
  public static void addInspections(@NotNull List<CodeSmellInspection> inspectionTools) {
    inspectionTools.add(new LongMethodInspection());
    inspectionTools.add(new LongParameterListInspection());
    inspectionTools.add(new MessageChainsInspection());
  }

  public static void resetToolWindow(final Project project) {
    ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Code Smell Checker");
    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    CSCToolWindow toolWindowPane = new CSCToolWindow(project);
    Content content = contentFactory.createContent(toolWindowPane.getContent(), null, false);

    ContentManager contentManager = toolWindow.getContentManager();

    contentManager.removeAllContents(true);
    contentManager.addContent(content);
  }
}
