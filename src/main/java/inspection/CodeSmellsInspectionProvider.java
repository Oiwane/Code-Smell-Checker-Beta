package inspection;

import com.intellij.codeInspection.InspectionToolProvider;
import inspection.codeSmell.LongMethodInspection;
import inspection.codeSmell.LongParameterListInspection;
import inspection.codeSmell.MessageChainsInspection;
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
