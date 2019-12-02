package ui.toolWindow.listener;

import com.intellij.openapi.project.Project;
import inspection.InspectionData;
import inspection.InspectionUtil;
import org.jetbrains.annotations.NotNull;
import ui.toolWindow.CSCToolWindowUtil;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

/**
 * ウィンドウの更新処理をするためのリスナークラス
 */
public class CSCToolWindowListener implements FocusListener {
  private Project myProject;
  private ArrayList<Integer> componentValueList;
  private ArrayList<InspectionData> inspectionDataList;

  public CSCToolWindowListener(@NotNull Project project) {
    myProject = project;
    componentValueList = new ArrayList<>();

    this.setInspectionDataList();
    this.setComponentValueList();
  }

  private void setInspectionDataList() {
    inspectionDataList = new ArrayList<>();

    inspectionDataList.add(new InspectionData(InspectionUtil.LONG_METHOD_PROPERTIES_COMPONENT_NAME, InspectionUtil.DEFAULT_NUM_PROCESSES));
    inspectionDataList.add(new InspectionData(InspectionUtil.LONG_PARAMETER_LIST_PROPERTIES_COMPONENT_NAME, InspectionUtil.DEFAULT_NUM_PARAMETER_LIST));
    inspectionDataList.add(new InspectionData(InspectionUtil.MESSAGE_CHAINS_PROPERTIES_COMPONENT_NAME, InspectionUtil.DEFAULT_NUM_CHAINS));
  }

  private void setComponentValueList() {
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

  /**
   * コードスメルの検出条件（Long MethodやLong ParameterListなど）が変更された場合、ツールウィンドウの更新を行う
   *
   * @param e [フォーカスイベント]
   */
  public void focusGained(FocusEvent e) {
    if (this.hasChangedComponentValue()) {
      this.setComponentValueList();
      CSCToolWindowUtil.resetToolWindow(myProject);
    }
  }

  public void focusLost(FocusEvent e) {
    // Nothing
  }

}
