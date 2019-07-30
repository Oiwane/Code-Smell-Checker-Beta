package Window;

import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollBar;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

import Source.SourceFileFetcher;

/**
 * コードスメル表示部の設定をする
 */
public class MyToolWindow {
    private Project myProject;
    private JPanel myToolWindowContent;
    private JTree sourceTree;
    private JScrollPane scrollPane;
    private DefaultTreeModel model;
    private DefaultMutableTreeNode root;

    /**
     * コンストラクタ
     * @param project [プロジェクト]
     */
    public MyToolWindow(Project project) {
        myProject = project;
        System.out.println(myProject.toString());

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
        myToolWindowContent = new JPanel();
        root = new DefaultMutableTreeNode("Java source code");
        model = new DefaultTreeModel(root);
        sourceTree = new Tree(root);
        scrollPane = new JBScrollPane(sourceTree);

//        String[] filenames = FilenameIndex.getAllFilenames(myProject);
//        for(PsiFile f : SourceFileFetcher.fetch(myProject, filenames)) {
//            this.updateSourceTree(f);
//        }

//        String[] filenames = FilenameIndex.getAllFilenames(myProject);
//        for(String f : filenames) {
//            this.updateSourceTree(f);
//        }

//        this.updateSourceTree(SourceFileFetcher.fetch(myProject));

//        for(PsiFile f : SourceFileFetcher.fetch(myProject)){
//            this.updateSourceTree(f.getName());
//        }

        this.setUIComponentsSize();
//        scrollPane.getViewport().setView(sourceTree);;
//        scrollPane.setPreferredSize(new Dimension(180, 120));

        myToolWindowContent.add(scrollPane);
//        myToolWindowContent.add(new JBScrollPane());
//        myToolWindowContent.add(new JLabel("Hello"));
    }

    /**
     * ウィンドウの要素のサイズを設定する
     */
    private void setUIComponentsSize() {
//        Dimension d = myToolWindowContent.getSize();
//        System.out.println(d);
        Dimension size = myToolWindowContent.getSize();
//        myToolWindowContent.setSize(size);
//        d = myToolWindowContent.getSize();
//        System.out.println(d);
        scrollPane.setSize(size);
        sourceTree.setSize(size);
    }
}