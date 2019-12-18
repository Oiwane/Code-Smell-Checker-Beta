package inspection;

import com.intellij.codeInspection.InspectionToolProvider;
import inspection.codeSmell.longMethod.LongMethodInspection;
import inspection.codeSmell.longParameterList.LongParameterListInspection;
import inspection.codeSmell.messageChains.MessageChainsInspection;
import org.jetbrains.annotations.NotNull;

/**
 * インスペクションを追加するためのクラス
 */
public class CodeSmellsInspectionProvider implements InspectionToolProvider {
  @NotNull
  @Override
  public Class[] getInspectionClasses() {
    return new Class[]{
      LongMethodInspection.class,
      LongParameterListInspection.class,
      MessageChainsInspection.class
    };
  }
}
