package window;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.projectView.impl.ProjectViewTree;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
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
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;
//import source.SourceFileFetcher;

import java.util.Collection;
//import source.MyFileCellRenderer;

/**
 * コードスメル表示部の設定をする
 */
public class MyToolWindow extends SimpleToolWindowPanel {
    private Project myProject;
    private DefaultTreeModel model;
    private DefaultMutableTreeNode root;
//    private Tree sourceTree;
    private ProjectViewTree sourceTree;

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
//        sourceTree = new Tree(root);
        sourceTree = new ProjectViewTree(model);
        JScrollPane scrollPane = new JBScrollPane(sourceTree);
//        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createMultipleFoldersDescriptor();
//
//        MyFileCellRenderer cellRenderer = new MyFileCellRenderer();
//
//        FileSystemTreeImpl fileSystemTree = new FileSystemTreeImpl(myProject, descriptor, sourceTree, cellRenderer, null, null);
//        sourceTree = (Tree) fileSystemTree.getTree();

        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, JavaFileType.INSTANCE, GlobalSearchScope.projectScope(myProject));

//        List<String> list = new ArrayList<String>();
//        File dir = new File(myProject.getProjectFilePath());
//        SourceFileFetcher.searchFile(list, dir);
//        for(String s : list) {
//            System.out.println(s);
//        }
//        ProjectViewPane
//        PsiFile[] files = SourceFileFetcher.fetch(myProject, list);
//        System.out.println("num of files : " + files.length);
//        for(PsiFile f : files) {
//            if(f == null) {
//                this.updateSourceTree("null");
//            } else {
//                this.updateSourceTree(f.toString());
//            }
//        }

        for(VirtualFile file : virtualFiles) {
//            this.updateSourceTree(PsiManager.getInstance(myProject).findViewProvider(file));
            PsiFile psiFile = PsiManager.getInstance(myProject).findFile(file);

            String projectPath =  myProject.getBasePath() + "/";
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(file.getPath().substring(projectPath.length()));
            DefaultTreeModel newModel = new DefaultTreeModel(newNode);
            newModel.insertNodeInto(new DefaultMutableTreeNode("aiueo"), newNode, newNode.getChildCount());
            root.add(newNode);

//            this.updateSourceTree(filename);
//            this.updateSourceTree(file.getName());
//            this.updateSourceTree(file.getUrl());
        }

        EditSourceOnDoubleClickHandler.install(sourceTree, new Runnable() {
            @Override
            public void run() {
                System.out.println("aiueo");
            }
        });

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
//
//    public void insertGrandchile(String parent, Object child) {
//        model = (DefaultTreeModel) sourceTree.getModel();
//        root = (DefaultMutableTreeNode) model.getRoot();
//
//    }
}