package ui.listener;

import com.intellij.codeInspection.InspectionProfile;
import com.intellij.codeInspection.ex.Tools;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.profile.ProfileChangeAdapter;
import inspection.CodeSmellInspection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ui.CSCToolWindowUtil;

import java.util.ArrayList;
import java.util.List;

public class CSCProfileChangeAdapter implements ProfileChangeAdapter {
  private Project myProject;

  public CSCProfileChangeAdapter(@NotNull Project project) {
    myProject = project;
  }

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

    CSCToolWindowUtil.resetToolWindow(myProject);
  }
}
