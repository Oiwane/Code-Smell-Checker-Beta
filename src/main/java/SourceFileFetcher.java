import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * プロジェクト内のJavaソースコードを取り込むためのクラス
 */
public class SourceFileFetcher {

    /**
     * プロジェクト内のJavaソースコードを取り込む
     */
    public static PsiFile[] fetch(Project project) {
        return FilenameIndex.getFilesByName(project, "*.java", GlobalSearchScope.projectScope(project));
    }
}
