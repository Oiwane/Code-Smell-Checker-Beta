package ui.listener;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.NotNull;
import ui.CSCToolWindowUtil;

/**
 * ファイル処理があった時にツールウィンドウを更新するためのクラス
 */
public class CSCVirtualFileListener implements VirtualFileListener {
  private Project myProject;

  public CSCVirtualFileListener(@NotNull Project project) { myProject = project; }

  public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
    VirtualFile eventFile = event.getFile();

    if (!this.isJavaFile(eventFile)) return;
    if (event.isFromSave()) {
      CSCToolWindowUtil.resetToolWindow(myProject);
    }
  }

  public void contentsChanged(@NotNull VirtualFileEvent event) {
    VirtualFile eventFile = event.getFile();

    if (!this.isJavaFile(eventFile)) return;
    for (VirtualFile file : ProjectRootManager.getInstance(myProject).getContentSourceRoots()) {
      if (eventFile.getName().contains(file.getName())) {
        if (event.isFromSave()) {
          CSCToolWindowUtil.resetToolWindow(myProject);
        }
      }
    }
  }

  public void beforePropertyChange(@NotNull VirtualFilePropertyEvent event) {
    if (event.isFromSave()) {
      CSCToolWindowUtil.resetToolWindow(myProject);
    }
  }

  public void beforeFileDeletion(@NotNull VirtualFileEvent event) {
    VirtualFile eventFile = event.getFile();

    if (!this.isJavaFile(eventFile)) return;
    CSCToolWindowUtil.resetToolWindow(myProject);
  }

  public void beforeFileMovement(@NotNull VirtualFileMoveEvent event) {
    VirtualFile eventFile = event.getFile();

    if (!this.isJavaFile(eventFile)) return;
    for (VirtualFile file : ProjectRootManager.getInstance(myProject).getContentSourceRoots()) {
      if (eventFile.getName().contains(file.getName())) {
        CSCToolWindowUtil.resetToolWindow(myProject);
      }
    }
  }

  /**
   * virtualFileがJavaファイルかどうかを返す
   * @brief virtualFileがディレクトリの場合、Javaファイルを持っているかを返す
   * @param virtualFile [変更があったファイル]
   * @return virtualFileがJavaファイルである、もしくはJavaファイルを持っていたらtrue
   */
  private boolean isJavaFile(@NotNull VirtualFile virtualFile) {
    if (!virtualFile.isDirectory()) {
      return virtualFile.getFileType() == JavaFileType.INSTANCE;
    }

    for (VirtualFile file : virtualFile.getChildren()) {
      if (isJavaFile(file)) return true;
    }

    return false;
  }
}
