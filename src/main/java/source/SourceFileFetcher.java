package source;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.w3c.dom.ls.LSInput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static PsiFile[] fetch(Project project, String[] filenames) {
        PsiFile[] result = new PsiFile[filenames.length];
        List<String> list = new ArrayList<String>();

        list.addAll(Arrays.asList(filenames));

        result = fetch(project, list);

        return result;
    }

    public static PsiFile[] fetch(Project project, List<String> filenames) {
        PsiFile[] result = new PsiFile[filenames.size()];

        int count = 0;
        for (String name : filenames) {
            PsiFile[] tmp = FilenameIndex.getFilesByName(project, name, GlobalSearchScope.projectScope(project));
            for (int i = 0; i < tmp.length; i++) {
                result[count + i] = tmp[i];
            }
        }

        return result;
    }

}
