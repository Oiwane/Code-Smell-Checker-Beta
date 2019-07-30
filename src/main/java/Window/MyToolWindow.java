package Window;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
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
import org.jetbrains.annotations.NotNull;

/**
 * コードスメル表示部の設定をする
 */
public class MyToolWindow extends SimpleToolWindowPanel {
    private Project myProject;
    private DefaultTreeModel model;
    private DefaultMutableTreeNode root;
    private Tree sourceTree;

    /**
     * コンストラクタ
     * @param project
     */
    public MyToolWindow(final Project project) {
        super(true, true);
        myProject = project;

        setToolbar(createToolbarPanel());
        setContent(createContentPanel());
    }

    /**
     * ツールバーのコンポーネントを作成する
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
     * @return ツールウィンドウにセットするスクロールペイン
     */
    private JScrollPane createContentPanel() {
        root = new DefaultMutableTreeNode("Java source code");
        model = new DefaultTreeModel(root);
        sourceTree = new Tree(root);
        JScrollPane scrollPane = new JBScrollPane(sourceTree);

        this.updateSourceTree("aiueo");
        this.updateSourceTree("abcd");

//        scrollPane.setViewportView(sourceTree);

        return scrollPane;
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
}