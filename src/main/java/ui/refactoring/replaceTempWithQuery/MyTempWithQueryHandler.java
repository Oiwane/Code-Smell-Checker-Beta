package ui.refactoring.replaceTempWithQuery;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.tempWithQuery.TempWithQueryHandler;
import org.jetbrains.annotations.NotNull;

public class MyTempWithQueryHandler {
    private final TempWithQueryHandler handler;
    private final boolean isTest;

    public MyTempWithQueryHandler(boolean isTest) {
        handler = new TempWithQueryHandler();
        this.isTest = isTest;
    }

    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
        if (!isTest)
            handler.invoke(project, elements, dataContext);
    }
}
