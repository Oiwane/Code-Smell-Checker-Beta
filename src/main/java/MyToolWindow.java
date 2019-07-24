import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

public class MyToolWindow {
    private Project project;
    private JPanel myToolWindowContent;
    private JTree sourceTree;
    private JScrollPane scrollPane;
    private DefaultTreeModel model;
    private DefaultMutableTreeNode root;


    public MyToolWindow(Project project_, ToolWindow toolWindow) {
        project = project_;
        myToolWindowContent = new JPanel();
        root = new DefaultMutableTreeNode("Java source code");
//        root = new DefaultMutableTreeNode();
        model = new DefaultTreeModel(root);
        sourceTree = new Tree(root);
        scrollPane = new JBScrollPane();
//        sourceTree = new Tree(root);
//        myToolWindowContent = new JPanel();
//        nodes = new DefaultMutableTreeNode("Java source code");

//        DefaultTreeModel model = (DefaultTreeModel) sourceTree.getModel();
//        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
//        model.insertNodeInto(new DefaultMutableTreeNode("nodes"), root, root.getChildCount());

        this.createUIComponents();
    }

    public void updateSourceTree(Object newNode) {
        model = (DefaultTreeModel) sourceTree.getModel();
        root = (DefaultMutableTreeNode) model.getRoot();
        model.insertNodeInto(new DefaultMutableTreeNode(newNode), root, root.getChildCount());
//        model.reload(root);
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

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

    private void setUIComponentsSize() {
        myToolWindowContent.setSize(myToolWindowContent.getMaximumSize());
        Dimension d = myToolWindowContent.getSize();
        System.out.println(d);
        scrollPane.setSize(myToolWindowContent.getSize());
        sourceTree.setSize(scrollPane.getSize());
    }
}