package ui;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.projectView.impl.ProjectViewTree;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
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
		actionGroup.add(new AnAction() {
			@Override
			public void actionPerformed(@NotNull AnActionEvent e) {

			}
		});
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

    Collection<VirtualFile> virtualFiles = FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.allScope(myProject));

    String projectPath = myProject.getBasePath() + "/";
    for (VirtualFile file : virtualFiles) {
      DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(file.getPath().substring(projectPath.length()));
      DefaultTreeModel newModel = new DefaultTreeModel(newNode);
      newModel.insertNodeInto(new DefaultMutableTreeNode("aiueo"), newNode, newNode.getChildCount());
      root.add(newNode);
    }

    EditSourceOnDoubleClickHandler.install(sourceTree, new Runnable() {
      @Override
      public void run() {
        // ダブルクリックした子ノードの親の名前を取得;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) sourceTree.getLastSelectedPathComponent();

        if (node == null) {
          System.out.println("Selected node is null");
          return;
        }
        if (node.getChildCount() != 0) {
          System.out.println("There are not code smells");
          return;
        }

        // 仮想ファイルを取得
        String openFilename = projectPath + node.getParent().toString();
        System.out.println(openFilename);
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(new File(openFilename));

        if (virtualFile == null) {
          System.out.println("Virtual file is null");
          return;
        }

        // TODO 未完成
        // ダブルクリックした子ノードの文字列を取得し、数値に変換
        String info = node.toString();
        System.out.println(info);

        // 仮想ファイルを開く
        OpenFileDescriptor descriptor = new OpenFileDescriptor(myProject, virtualFile, 1, 1);
        descriptor.navigateInEditor(myProject, true);
      }
    });

    return scrollPane;
	}

//    /**
//     * ソースコードをツリーに入れ込む
//     *
//     * @param newNode [オブジェクト]
//     */
//    public void updateSourceTree(Object newNode) {
//        model = (DefaultTreeModel) sourceTree.getModel();
//        root = (DefaultMutableTreeNode) model.getRoot();
//        model.insertNodeInto(new DefaultMutableTreeNode(newNode), root, root.getChildCount());
//    }
//
//    public void insertGrandchile(String parent, Object child) {
//        model = (DefaultTreeModel) sourceTree.getModel();
//        root = (DefaultMutableTreeNode) model.getRoot();
//
//    }
}