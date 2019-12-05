package ui.toolWindow;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.ide.dnd.aware.DnDAwareTree;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.messages.MessageDialog;
import com.intellij.openapi.vfs.*;
import com.intellij.profile.codeInspection.ProjectInspectionProfileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.intellij.util.EditSourceOnDoubleClickHandler;
import inspection.CodeSmellInspection;
import inspection.InspectionUtil;
import org.jetbrains.annotations.NotNull;
import ui.toolWindow.listener.CSCProfileChangeAdapter;
import ui.toolWindow.listener.CSCToolWindowListener;
import ui.toolWindow.listener.CSCVirtualFileListener;

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

    virtualFiles = new ArrayList<>();
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
    for (VirtualFile file : FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.projectScope(myProject))) {
      if (!ProjectRootManager.getInstance(myProject).getFileIndex().isInSource(file)) continue;
      virtualFiles.add(file);
    }

    this.createTree();

    EditSourceOnDoubleClickHandler.install(sourceTree, new CSCDoubleClickListener());

    // リスナーの登録
    VirtualFileManager.getInstance().addVirtualFileListener(new CSCVirtualFileListener(myProject));
    ProjectInspectionProfileManager.getInstance(myProject).addProfileChangeListener(new CSCProfileChangeAdapter(myProject), myProject);
    sourceTree.addFocusListener(new CSCToolWindowListener(myProject));

    return scrollPane;
	}

  /**
   * ツールウィンドウ内のツリーを生成する
   */
  private void createTree() {
    List<CodeSmellInspection> inspectionTools = new ArrayList<>();
    CSCToolWindowUtil.addInspections(inspectionTools);

    String projectPath = myProject.getBasePath() + "/";

    for (VirtualFile file : virtualFiles) {
      fileTreeNode = new DefaultMutableTreeNode(file.getPath().substring(projectPath.length()));
      fileTreeModel = new DefaultTreeModel(fileTreeNode);

      PsiFile psiFile = PsiManager.getInstance(myProject).findFile(file);

      this.insertCodeSmellInfo(inspectionTools, InspectionManager.getInstance(myProject), psiFile);
    }
  }

  /**
   * ファイルにコードスメルがあればツリーに情報を挿入する
   *
   * @param inspectionTools [自作インスペクションのリスト]
   * @param manager [インスペクションマネージャー]
   * @param psiFile [psiファイル]
   */
  private void insertCodeSmellInfo(@NotNull List<CodeSmellInspection> inspectionTools, InspectionManager manager, PsiFile psiFile) {
    for (CodeSmellInspection inspectionTool : inspectionTools) {
      if (!this.getWorkedInspection(inspectionTool.getWorked())) return;
      if (psiFile == null) continue;

      List<ProblemDescriptor> codeSmellList = inspectionTool.processFile(psiFile, manager);

      // コードスメルが無かったらスルー
      if (codeSmellList.size() == 0) continue;

      DefaultMutableTreeNode codeSmellTreeNode = new DefaultMutableTreeNode(inspectionTool.getDisplayName());
      DefaultTreeModel codeSmellTreeModel = new DefaultTreeModel(codeSmellTreeNode);
      fileTreeModel.insertNodeInto(codeSmellTreeNode, fileTreeNode, fileTreeNode.getChildCount());

      for (ProblemDescriptor descriptor : codeSmellList) {
        int startLine = descriptor.getLineNumber() + 1;

        DefaultMutableTreeNode openTreeNode = new DefaultMutableTreeNode("Line : " + startLine);

        codeSmellTreeModel.insertNodeInto(openTreeNode, codeSmellTreeNode, codeSmellTreeNode.getChildCount());

        root.add(fileTreeNode);
      }
    }
  }

  private boolean getWorkedInspection(String propertiesComponentName) {
    String value = PropertiesComponent.getInstance().getValue(propertiesComponentName);
    if (value != null) {
      return Boolean.valueOf(value);
    } else {
      return InspectionUtil.IS_ENABLED_BY_DEFAULT;
    }
  }

  /**
   * ノードをダブルクリックした時の動作を管理する
   */
	private class CSCDoubleClickListener implements Runnable {
	  @Override
    public void run() {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) sourceTree.getLastSelectedPathComponent();

      assert node != null : "Node does not exist.";

      if (node.getParent() == null) {
        String message = "Child node does not exist.\n" +
                         "The following reasons will be listed\n" +
                         "1. Code smells do not exist.\n" +
                         "2. You have not set up such as the project SDK.\n" +
                         "In case of 2, set in \"Project Structure\"";
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
      String lineInfo = node.toString();
      int line = Integer.parseInt(lineInfo.substring(("Line : ").length()));

      // ファイルを開く
      OpenFileDescriptor descriptor = new OpenFileDescriptor(myProject, virtualFile, line - 1, 0);
      descriptor.navigateInEditor(myProject, true);
    }
	}
}