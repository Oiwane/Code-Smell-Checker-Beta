package ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import inspection.InspectionData;
import org.jetbrains.annotations.NotNull;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import static inspection.InspectionUtil.*;

/**
 * ウィンドウの更新処理をするためのリスナークラス
 */
public class RefactoringNavigatorToolWindowListener implements FocusListener, VirtualFileListener {
  private Project myProject;
  private ArrayList<Integer> componentValueList;
  private ArrayList<InspectionData> inspectionDataList;
  private ContentManager contentManager;
  private Content content;

  RefactoringNavigatorToolWindowListener(@NotNull Project project) {
    myProject = project;

    this.setInspectionDataList();
    this.setComponentValueList();
  }

  private void setInspectionDataList() {
    inspectionDataList = new ArrayList<>();

    inspectionDataList.add(new InspectionData(LONG_METHOD_PROPERTIES_COMPONENT_NAME, DEFAULT_NUM_PROCESSES));
    inspectionDataList.add(new InspectionData(LONG_PARAMETER_LIST_PROPERTIES_COMPONENT_NAME, DEFAULT_NUM_PARAMETER_LIST));
    inspectionDataList.add(new InspectionData(MESSAGE_CHAINS_PROPERTIES_COMPONENT_NAME, DEFAULT_NUM_CHAINS));
  }

  private void setComponentValueList() {
    componentValueList = new ArrayList<>();

    for (InspectionData data : inspectionDataList) {
      componentValueList.add(getUpperLimitValue(data));
    }
  }

  private boolean hasChangedComponentValue() {
    for (int i = 0; i < inspectionDataList.size(); i++) {
      int oldComponentValue = componentValueList.get(i);
      int currentComponentValue = getUpperLimitValue(inspectionDataList.get(i));
      if (oldComponentValue != currentComponentValue) {
        return true;
      }
    }

    return false;
  }

  private void resetToolWindow() {
    contentManager.removeAllContents(true);
    contentManager.addContent(content);

    this.setComponentValueList();
  }

  //// ここからFocusListener

  /**
   * コードスメルの検出条件（Long MethodやLong ParameterListなど）が変更された場合、ツールウィンドウの更新を行う
   *
   * @param e [フォーカスイベント]
   */
  public void focusGained(FocusEvent e) {
    ToolWindow toolWindow = ToolWindowManager.getInstance(myProject).getToolWindow("Code Smell Checker");
    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    RefactoringNavigatorToolWindow toolWindowPane = new RefactoringNavigatorToolWindow(myProject);
    content = contentFactory.createContent(toolWindowPane.getContent(), null, false);

    contentManager = toolWindow.getContentManager();

    if (this.hasChangedComponentValue()) {
      resetToolWindow();
    }
  }

  public void focusLost(FocusEvent e) {
    // Nothing
  }

  //// ここからVirtualFileListener
  public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
    resetToolWindow();
  }

  public void contentsChanged(@NotNull VirtualFileEvent event) {
    resetToolWindow();
  }

  public void fileCreated(@NotNull VirtualFileEvent event) {
    resetToolWindow();
  }

  public void fileDeleted(@NotNull VirtualFileEvent event) {
    resetToolWindow();
  }

  public void fileMoved(@NotNull VirtualFileMoveEvent event) {
    resetToolWindow();
  }

  public void fileCopied(@NotNull VirtualFileCopyEvent event) {
    resetToolWindow();
  }

  public void beforePropertyChange(@NotNull VirtualFilePropertyEvent event) {
    resetToolWindow();
  }

  public void beforeContentsChange(@NotNull VirtualFileEvent event) {
    resetToolWindow();
  }

  public void beforeFileDeletion(@NotNull VirtualFileEvent event) {
    resetToolWindow();
  }

  public void beforeFileMovement(@NotNull VirtualFileMoveEvent event) {
    resetToolWindow();
  }
}
