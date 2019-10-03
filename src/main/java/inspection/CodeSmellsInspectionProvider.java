package inspection;

import com.intellij.codeInspection.InspectionToolProvider;
import inspection.longMethod.LongMethodInspection;
import inspection.longParameterList.LongParameterListInspection;
import inspection.messageChains.MessageChainsInspection;
import org.jetbrains.annotations.NotNull;

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
