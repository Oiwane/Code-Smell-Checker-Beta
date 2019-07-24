import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

/**
 * コードスメル表示部の設定をする
 */
public class MyToolWindow {
    private Project project;
    private JPanel myToolWindowContent;
    private JTree sourceTree;
    private JScrollPane scrollPane;
    private DefaultTreeModel model;
    private DefaultMutableTreeNode root;

    /**
     * コンストラクタ
     * @param project_ [プロジェクト]
     * @param toolWindow [ツールウィンドウ]
     */
    public MyToolWindow(Project project_, ToolWindow toolWindow) {
        project = project_;
        myToolWindowContent = new JPanel();
        root = new DefaultMutableTreeNode("Java source code");
//        root = new DefaultMutableTreeNode();
        model = new DefaultTreeModel(root);
        sourceTree = new Tree(root);
        scrollPane = new JBScrollPane();

        this.createUIComponents();
    }

    /**
     * ソースコードをツリーに入れ込む
     * @param newNode [オブジェクト]
     */
    public void updateSourceTree(Object newNode) {
        model = (DefaultTreeModel) sourceTree.getModel();
        root = (DefaultMutableTreeNode) model.getRoot();
        model.insertNodeInto(new DefaultMutableTreeNode(newNode), root, root.getChildCount());
//        model.reload(root);
    }

    /**
     * ゲッター
     * @return JPanel [ウィンドウの構成要素を含んだパネル]
     */
    public JPanel getContent() {
        return myToolWindowContent;
    }

    /**
     * ウィンドウの要素を作成する
     */
    private void createUIComponents() {
        // TODO: place custom component creation code here
//        String[] filenames = FilenameIndex.getAllFilenames(project);
//        this.updateSourceTree(SourceFileFetcher.fetch(project));
        for(PsiFile f : SourceFileFetcher.fetch(project)){
            this.updateSourceTree(f.getName());
        }

        this.setUIComponentsSize();
        scrollPane.setViewportView(sourceTree);
//        this.updateSourceTree("Java source code");
//        model = (DefaultTreeModel) sourceTree.getModel();
//        root = (DefaultMutableTreeNode) model.getRoot();
//        root.add(new DefaultMutableTreeNode("Java source code"));
//        model.reload(root);

//        JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setTitle("Refactoring for Java");
//        frame.setVisible(true);

        myToolWindowContent.add(scrollPane);
//        myToolWindowContent.add(sourceTree);

//        frame.getContentPane().add(myToolWindowContent, BorderLayout.LINE_START);
    }

    /**
     * ウィンドウの要素のサイズを設定する
     */
    private void setUIComponentsSize() {
        myToolWindowContent.setSize(myToolWindowContent.getMaximumSize());
        Dimension d = myToolWindowContent.getSize();
        System.out.println(d);
        scrollPane.setSize(myToolWindowContent.getSize());
        sourceTree.setSize(scrollPane.getSize());
    }
}