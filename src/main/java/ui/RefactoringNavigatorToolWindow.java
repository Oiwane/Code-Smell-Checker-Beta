package ui;

import action.RefactoringNavigatorToolWindowAction;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.projectView.impl.ProjectViewTree;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.intellij.util.EditSourceOnDoubleClickHandler;
import inspection.longMethod.LongMethodInspection;
import inspection.longParameterList.LongParameterListInspection;
import inspection.messageChains.MessageChainsInspection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * コードスメル表示部の設定をする
 */
public class RefactoringNavigatorToolWindow extends SimpleToolWindowPanel {
	private final Project myProject;
  private DefaultMutableTreeNode root;
	// TODO ツリーが更新されない
	private ProjectViewTree sourceTree;
  private Collection<VirtualFile> virtualFiles;

	/**
	 * コンストラクタ
	 *
	 * @param project [プロジェクト]
	 */
	public RefactoringNavigatorToolWindow(final Project project) {
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
	@NotNull
  private JComponent createToolbarPanel() {
	  final DefaultActionGroup actionGroup = new DefaultActionGroup();
		actionGroup.add(new RefactoringNavigatorToolWindowAction());
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
    DefaultTreeModel model = new DefaultTreeModel(root);
    sourceTree = new ProjectViewTree(model);
    JScrollPane scrollPane = new JBScrollPane(sourceTree);
    virtualFiles = FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.projectScope(myProject));

    this.createTree();

    EditSourceOnDoubleClickHandler.install(sourceTree, new MyToolWindowRunnable());

    return scrollPane;
	}

  /**
   * ツールウィンドウ内のツリーを生成する
   */
  private void createTree() {
    List<AbstractBaseJavaLocalInspectionTool> inspectionTools = new ArrayList<>();
    InspectionManager manager = InspectionManager.getInstance(myProject);

    inspectionTools.add(new LongMethodInspection());
    inspectionTools.add(new LongParameterListInspection());
    inspectionTools.add(new MessageChainsInspection());

    String projectPath = myProject.getBasePath() + "/";

    for (VirtualFile file : virtualFiles) {
      DefaultMutableTreeNode fileTreeNode = new DefaultMutableTreeNode(file.getPath().substring(projectPath.length()));
      DefaultTreeModel fileTreeModel = new DefaultTreeModel(fileTreeNode);

      PsiFile psi = PsiManager.getInstance(myProject).findFile(file);
      List<ProblemDescriptor> codeSmellList;

      // コードスメルが何行目にあるのかを探す
      for (AbstractBaseJavaLocalInspectionTool inspectionTool : inspectionTools) {
        if (psi == null) continue;
        String codeSmellName = inspectionTool.getDisplayName();
        DefaultMutableTreeNode codeSmellTreeNode = new DefaultMutableTreeNode(codeSmellName);
        DefaultTreeModel codeSmellTreeModel = new DefaultTreeModel(codeSmellTreeNode);

        codeSmellList = inspectionTool.processFile(psi, manager);

        if (codeSmellList.size() == 0) continue;
        fileTreeModel.insertNodeInto(codeSmellTreeNode, fileTreeNode, fileTreeNode.getChildCount());

        // ファイルにコードスメルがあればツリーにファイル情報を挿入する
        for (ProblemDescriptor descriptor : codeSmellList) {
          int firstLine = descriptor.getLineNumber() + 1;

          DefaultMutableTreeNode openTreeNode = new DefaultMutableTreeNode("Line : " + firstLine);

          codeSmellTreeModel.insertNodeInto(openTreeNode, codeSmellTreeNode, codeSmellTreeNode.getChildCount());

          root.add(fileTreeNode);
        }
      }
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

      String openFilename = myProject.getBasePath() + "/" + node.getParent().getParent().toString();
      VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(new File(openFilename));

      if (virtualFile == null) {
        System.out.println("Virtual file is null");
        return;
      }

      // ダブルクリックした子ノードの文字列から行数を取得する
      String info = node.toString();
      int line = Integer.parseInt(info.substring(("Line : ").length()));

      // ファイルを開く
      OpenFileDescriptor descriptor = new OpenFileDescriptor(myProject, virtualFile, line - 1, 0);
      descriptor.navigateInEditor(myProject, true);
    }
	}
}