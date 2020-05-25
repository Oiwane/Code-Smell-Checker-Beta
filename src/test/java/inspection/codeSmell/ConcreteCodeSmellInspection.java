package inspection.codeSmell;

import inspection.CodeSmellInspection;
import org.jetbrains.annotations.NotNull;

public class ConcreteCodeSmellInspection extends CodeSmellInspection {
    @NotNull
    @Override
    public String getDisplayName() {
        return "Null";
    }
}
