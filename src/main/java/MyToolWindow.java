import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

public class MyToolWindow {
    private JPanel myToolWindowContent;
    private JScrollPane scrollPane;
    private JTree sourceTree;
    private DefaultTreeModel model;
    private DefaultMutableTreeNode root;


    public MyToolWindow(ToolWindow toolWindow) {
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
        model.reload();
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        scrollPane.setViewportView(sourceTree);
//        this.updateSourceTree("Java source code");
        model = (DefaultTreeModel) sourceTree.getModel();
        root = (DefaultMutableTreeNode) model.getRoot();
        root.add(new DefaultMutableTreeNode("Java source code"));
        model.reload(root);

//        JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setTitle("Refactoring for Java");
//        frame.setVisible(true);

        myToolWindowContent.add(scrollPane);

//        frame.getContentPane().add(myToolWindowContent, BorderLayout.LINE_START);
    }
}