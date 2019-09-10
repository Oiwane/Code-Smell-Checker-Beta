package ui;

import action.MyToolWindowAction;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.projectView.impl.ProjectViewTree;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.intellij.util.EditSourceOnDoubleClickHandler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

/**
 * コードスメル表示部の設定をする
 */
public class MyToolWindow extends SimpleToolWindowPanel {
	private Project myProject;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	// TODO ツリーが更新されない
	private ProjectViewTree sourceTree;

	/**
	 * コンストラクタ
	 *
	 * @param project
	 */
	public MyToolWindow(final Project project) {
		super(true, true);
		myProject = project;

		this.setToolbar(createToolbarPanel());
		this.setContent(createContentPanel());
	}

	/**
	 * ツールバーのコンポーネントを作成する
	 *
	 * @return ツールバーのコンポーネント
	 */
	private JComponent createToolbarPanel() {
	  final DefaultActionGroup actionGroup = new DefaultActionGroup();
		actionGroup.add(new MyToolWindowAction());
		final ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("sample", actionGroup, true);
		return actionToolbar.getComponent();
	}

	/**
	 * ツールウィンドウのコンポーネントを作成する
	 *
	 * @return ツールウィンドウにセットするスクロールペイン
	 */
	private JScrollPane createContentPanel() {
    root = new DefaultMutableTreeNode("Java source code");
    model = new DefaultTreeModel(root);
    sourceTree = new ProjectViewTree(model);
    JScrollPane scrollPane = new JBScrollPane(sourceTree);

    this.createTree();

    EditSourceOnDoubleClickHandler.install(sourceTree, new MyToolWindowRunnable());

    return scrollPane;
	}

  /**
   * ツールウィンドウ内のツリーを生成する
   */
  private void createTree() {
    Collection<VirtualFile> virtualFiles = FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.allScope(myProject));

    String projectPath = myProject.getBasePath() + "/";

    for (VirtualFile file : virtualFiles) {

      // TODO コードスメルがない場合の処理を書く
      DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(file.getPath().substring(projectPath.length()));
      DefaultTreeModel newModel = new DefaultTreeModel(newNode);
      // TODO コードスメルが何行目にあるのかを探す
      newModel.insertNodeInto(new DefaultMutableTreeNode("aiueo"), newNode, newNode.getChildCount());
      root.add(newNode);
    }
  }

  /**
   * ノードをダブルクリックした時の動作を管理する
   */
	private class MyToolWindowRunnable implements Runnable {
	  private MyToolWindowRunnable() {}

	  @Override
    public void run() {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) sourceTree.getLastSelectedPathComponent();

      if (node == null) {
        System.out.println("Selected node is null");
        return;
      }
      if (node.getChildCount() != 0) {
        System.out.println("There are not code smells");
        return;
      }

      String openFilename = myProject.getBasePath() + "/" + node.getParent().toString();
      // System.out.println(openFilename);
      VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(new File(openFilename));

      if (virtualFile == null) {
        System.out.println("Virtual file is null");
        return;
      }

      PsiFile psi = PsiManager.getInstance(myProject).findFile(virtualFile);
      PsiJavaFile psiJavaFile = (PsiJavaFile) PsiManager.getInstance(myProject).findFile(virtualFile);
      PsiElement element = psi.findElementAt(1);
      System.out.println(psiJavaFile);
      System.out.println(element);

      // TODO ダブルクリックした子ノードの文字列を取得し、数値に変換する
      String info = node.toString();
      System.out.println(info);

      OpenFileDescriptor descriptor = new OpenFileDescriptor(myProject, virtualFile, 1, 1);
      descriptor.navigateInEditor(myProject, true);
    }
	}
}