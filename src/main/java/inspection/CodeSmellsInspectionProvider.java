package inspection;

import com.intellij.codeInspection.InspectionToolProvider;
import inspection.LongMethod.LongMethodInspection;
import inspection.LongParameterList.LongParameterListInspection;
import inspection.MessageChains.MessageChainsInspection;
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
