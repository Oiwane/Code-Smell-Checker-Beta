package ui;

import com.intellij.codeInspection.InspectionProfile;
import com.intellij.codeInspection.ex.Tools;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.profile.ProfileChangeAdapter;
import com.intellij.profile.codeInspection.ProjectInspectionProfileManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import inspection.CodeSmellInspection;
import inspection.InspectionData;
import inspection.InspectionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;

/**
 * ウィンドウの更新処理をするためのリスナークラス
 */
public class CSCToolWindowListener implements FocusListener, VirtualFileListener {
  private Project myProject;
  private ArrayList<Integer> componentValueList;
  private ArrayList<InspectionData> inspectionDataList;

  CSCToolWindowListener(@NotNull Project project) {
    myProject = project;

    this.setInspectionDataList();
    this.setComponentValueList();

    ProjectInspectionProfileManager.getInstance(myProject).addProfileChangeListener(new CSCProfileChangeAdapter(), myProject);
  }

  private void setInspectionDataList() {
    inspectionDataList = new ArrayList<>();

    inspectionDataList.add(new InspectionData(InspectionUtil.LONG_METHOD_PROPERTIES_COMPONENT_NAME, InspectionUtil.DEFAULT_NUM_PROCESSES));
    inspectionDataList.add(new InspectionData(InspectionUtil.LONG_PARAMETER_LIST_PROPERTIES_COMPONENT_NAME, InspectionUtil.DEFAULT_NUM_PARAMETER_LIST));
    inspectionDataList.add(new InspectionData(InspectionUtil.MESSAGE_CHAINS_PROPERTIES_COMPONENT_NAME, InspectionUtil.DEFAULT_NUM_CHAINS));
  }

  private void setComponentValueList() {
    componentValueList = new ArrayList<>();

    for (InspectionData data : inspectionDataList) {
      componentValueList.add(InspectionUtil.getUpperLimitValue(data));
    }
  }

  private boolean hasChangedComponentValue() {
    for (int i = 0; i < inspectionDataList.size(); i++) {
      int oldComponentValue = componentValueList.get(i);
      int currentComponentValue = InspectionUtil.getUpperLimitValue(inspectionDataList.get(i));
      if (oldComponentValue != currentComponentValue) {
        return true;
      }
    }

    return false;
  }

  private void resetToolWindow() {
    ToolWindow toolWindow = ToolWindowManager.getInstance(myProject).getToolWindow("Code Smell Checker");
    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    CSCToolWindow toolWindowPane = new CSCToolWindow(myProject);
    Content content = contentFactory.createContent(toolWindowPane.getContent(), null, false);

    ContentManager contentManager = toolWindow.getContentManager();

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

  private class CSCProfileChangeAdapter implements ProfileChangeAdapter {
    @Override
    public void profileChanged(@Nullable InspectionProfile profile) {
      if (profile == null) return;

      // Applyボタンを押すまでprofile名の語尾に"(copy)"と付くため、それでApplyボタンを押したかを確認する
      int startIndex = profile.getName().length();
      if (profile.toString().substring(startIndex).equals(" (copy)")) return;

      List<Tools> toolsList = profile.getAllEnabledInspectionTools(myProject);

      // 自作インスペクションのリストを作成
      List<CodeSmellInspection> inspectionTools = new ArrayList<>();
      CSCToolWindowUtil.addInspections(inspectionTools);

      for (CodeSmellInspection inspection : inspectionTools) {
        PropertiesComponent.getInstance().setValue(inspection.getWorked(), "false");
      }

      for (Tools tools : toolsList) {
        for (CodeSmellInspection inspection : inspectionTools) {
          // toolsが自作インスペクションかどうかの判定
          if (tools.getShortName().equals(inspection.getShortName())) {
            PropertiesComponent.getInstance().setValue(inspection.getWorked(), "true");
          }
        }
      }

      resetToolWindow();
    }
  }
}
