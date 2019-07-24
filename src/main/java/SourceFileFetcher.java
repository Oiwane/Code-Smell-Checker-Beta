import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

public class SourceFileFetcher {

    public static PsiFile[] fetch(Project project) {
        return FilenameIndex.getFilesByName(project, "*.java", GlobalSearchScope.projectScope(project));
    }
}
