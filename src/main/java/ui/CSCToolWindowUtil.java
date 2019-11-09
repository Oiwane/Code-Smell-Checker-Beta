package ui;

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
}
