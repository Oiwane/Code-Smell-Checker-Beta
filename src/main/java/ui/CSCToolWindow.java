package ui;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.ide.dnd.aware.DnDAwareTree;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.messages.MessageDialog;
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
import java.util.List;

/**
 * コードスメル表示部の設定をする
 */
public class CSCToolWindow extends SimpleToolWindowPanel {
	private final Project myProject;
  private DefaultMutableTreeNode root;
	private DnDAwareTree sourceTree;
  private ArrayList<VirtualFile> virtualFiles;
  private DefaultMutableTreeNode fileTreeNode; //psiファイルに対応したツリーノード
  private DefaultTreeModel fileTreeModel; //psiファイルに対応したツリーモデル

  /**
	 * コンストラクタ
	 *
	 * @param project [プロジェクト]
	 */
	public CSCToolWindow(final Project project) {
		super(true, true);
		myProject = project;

		this.setContent(createContentPanel());
	}

	/**
	 * ツールウィンドウのコンポーネントを作成する
	 *
	 * @return ツールウィンドウにセットするスクロールペイン
	 */
	private JScrollPane createContentPanel() {
    root = new DefaultMutableTreeNode("Java source code");
    sourceTree = new DnDAwareTree(new DefaultTreeModel(root));
    JScrollPane scrollPane = new JBScrollPane(sourceTree);

    // ツールウィンドウに表示するvirtualFileの取得
    virtualFiles = new ArrayList<>();
    for (VirtualFile file : FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.projectScope(myProject))) {
      if (!ProjectRootManager.getInstance(myProject).getFileIndex().isInSource(file)) continue;
      virtualFiles.add(file);
    }

    this.createTree();

    EditSourceOnDoubleClickHandler.install(sourceTree, new MyToolWindowRunnable());

    CSCToolWindowListener listener = new CSCToolWindowListener(myProject);
    sourceTree.addFocusListener(listener);

    return scrollPane;
	}

  /**
   * ツールウィンドウ内のツリーを生成する
   */
  private void createTree() {
    List<AbstractBaseJavaLocalInspectionTool> inspectionTools = new ArrayList<>();
    InspectionManager manager = InspectionManager.getInstance(myProject);

    this.addInspections(inspectionTools);

    String projectPath = myProject.getBasePath() + "/";

    for (VirtualFile file : virtualFiles) {
      fileTreeNode = new DefaultMutableTreeNode(file.getPath().substring(projectPath.length()));
      fileTreeModel = new DefaultTreeModel(fileTreeNode);

      PsiFile psiFile = PsiManager.getInstance(myProject).findFile(file);

      this.findCodeSmells(inspectionTools, manager, psiFile);
    }
  }

  /**
   * コードスメルが何行目にあるのかを探す
   *
   * @param inspectionTools [自作インスペクションのリスト]
   * @param manager [インスペクションマネージャー]
   * @param psiFile [psiファイル]
   */
  private void findCodeSmells(@NotNull List<AbstractBaseJavaLocalInspectionTool> inspectionTools, InspectionManager manager, PsiFile psiFile) {
    for (AbstractBaseJavaLocalInspectionTool inspectionTool : inspectionTools) {
      if (psiFile == null) continue;
      String codeSmellName = inspectionTool.getDisplayName();
      DefaultMutableTreeNode codeSmellTreeNode = new DefaultMutableTreeNode(codeSmellName);
      DefaultTreeModel codeSmellTreeModel = new DefaultTreeModel(codeSmellTreeNode);

      List<ProblemDescriptor> codeSmellList = inspectionTool.processFile(psiFile, manager);
      
      if (codeSmellList.size() == 0) continue;
      fileTreeModel.insertNodeInto(codeSmellTreeNode, fileTreeNode, fileTreeNode.getChildCount());

      this.addCodeSmellsInfo(codeSmellList, codeSmellTreeNode, codeSmellTreeModel);
    }
  }

  /**
   * ファイルにコードスメルがあればツリーにファイル情報を挿入する
   *
   * @param codeSmellList [コードスメルのある箇所を格納するリスト]
   * @param codeSmellTreeNode [コードスメルに対応したツリーノード]
   * @param codeSmellTreeModel [コードスメルに対応したツリーモデル]
   */
  private void addCodeSmellsInfo(@NotNull List<ProblemDescriptor> codeSmellList, DefaultMutableTreeNode codeSmellTreeNode, DefaultTreeModel codeSmellTreeModel) {
    for (ProblemDescriptor descriptor : codeSmellList) {
      int firstLine = descriptor.getLineNumber() + 1;

      DefaultMutableTreeNode openTreeNode = new DefaultMutableTreeNode("Line : " + firstLine);

      codeSmellTreeModel.insertNodeInto(openTreeNode, codeSmellTreeNode, codeSmellTreeNode.getChildCount());

      root.add(fileTreeNode);
    }
  }

  /**
   * リストにインスペクションを追加する
   *
   * @param inspectionTools [自作インスペクションのリスト]
   */
  private void addInspections(@NotNull List<AbstractBaseJavaLocalInspectionTool> inspectionTools) {
    inspectionTools.add(new LongMethodInspection());
    inspectionTools.add(new LongParameterListInspection());
    inspectionTools.add(new MessageChainsInspection());
  }

  /**
   * ノードをダブルクリックした時の動作を管理する
   */
	private class MyToolWindowRunnable implements Runnable {
	  @Override
    public void run() {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) sourceTree.getLastSelectedPathComponent();

      assert node != null : "Node does not exist.";

      if (node.getParent() == null) {
        String message = "Code smells does not exist or this project has not been set up. If the latter, set up in \"Project Structure\".";
        MessageDialog dialog = new MessageDialog(message, "Message", new String[]{"OK"}, 1, null);
        dialog.show();
        return;
      }

      String openFilename = myProject.getBasePath() + "/" + node.getParent().getParent().toString();
      VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(new File(openFilename));

      if (virtualFile == null) {
        MessageDialog dialog = new MessageDialog("This file does not exist.", "Error", new String[]{"OK"}, 1, null);
        dialog.show();
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