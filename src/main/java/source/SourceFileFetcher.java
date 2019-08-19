package source;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.io.File;
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
    public static PsiFile[] fetch(@NotNull Project project) {
        return FilenameIndex.getFilesByName(project, "*.java", GlobalSearchScope.projectScope(project));
    }

    public static PsiFile[] fetch(@NotNull Project project, String[] filenames) {
        PsiFile[] result = new PsiFile[filenames.length];
        List<String> list = new ArrayList<String>();

        list.addAll(Arrays.asList(filenames));

        result = fetch(project, list);

        return result;
    }

    public static PsiFile[] fetch(@NotNull Project project, List<String> filenames) {
        PsiFile[] result = new PsiFile[filenames.size()];

        int count = 0;
        for (String name : filenames) {
            PsiFile[] tmp = FilenameIndex.getFilesByName(project, name, GlobalSearchScope.projectScope(project));
            for (int i = 0; i < tmp.length; i++) {
                result[count + i] = tmp[i];
                count++;
            }
        }

        return result;
    }

    /**
     * ファイル名をlistに格納していく
     *
     * @param list [ファイル名を格納していくやつ]
     * @param dir  [targetディレクトリのパス]
     */
    public static void searchFile(List<String> list, File dir) {
        File[] fileList = dir.listFiles();

        if (fileList != null) {
            for (File f : fileList) {
                if (f.isDirectory()) {
                    File fileList_ = new File(f.toString());
                    searchFile(list, fileList_);
                } else if (f.isFile()) {
                    list.add(f.toString());
//                    list.add(f.getName());
                }
            }
        } else {
            System.out.println("ファイルがありません。");
        }
    }

    /**
     * targetディレクトリ内のファイル名を取得する
     *
     * @param list [ファイル名格納用List]
     * @return [編集したファイル名を保持した変数]
     */
    public String[] fetchFileName(List<String> list) {
        String[] fileNames = new String[list.size()];

        for (int i = 0; i < list.size(); i++) {
            // ファイル名に"./target"が付いているのでそれを省く
            fileNames[i] = list.get(i).substring(("./").length());
        }

        return fileNames;
    }

}
