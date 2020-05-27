package refactoring;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiStatement;
import org.jetbrains.annotations.Contract;
import visitor.TargetElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RefactoringUtil {
    @Contract(pure = true)
    private RefactoringUtil() {
    }

    /**
     * 使用しているパラメータをメソッド呼び出しに置き換える
     *
     * @param method          変更するメソッド
     * @param targetParameter 対象のパラメータ
     * @param newElement      パラメータの代わりに置くメソッド呼び出し
     */
    public static void replaceParameterObject(@NotNull PsiMethod method, @NotNull PsiParameter targetParameter, PsiExpression newElement) {
        TargetElementVisitor visitor = new TargetElementVisitor(targetParameter);

        final PsiCodeBlock codeBlock = method.getBody();
        assert codeBlock != null;
        if (codeBlock.getStatements().length == 0) {
            return;
        }

        codeBlock.accept(visitor);
        final List<PsiElement> elementList = visitor.getElementList();

        WriteCommandAction.runWriteCommandAction(targetParameter.getProject(), () -> {
            if (elementList.size() > 1) {
                PsiElementFactory factory = PsiElementFactory.getInstance(targetParameter.getProject());
                PsiDeclarationStatement declarationStatement = factory.createVariableDeclarationStatement(targetParameter.getName(), targetParameter.getType(), newElement);

                PsiStatement firstStatement = codeBlock.getStatements()[0];
                codeBlock.addBefore(declarationStatement, firstStatement);
            } else if (elementList.size() == 1) {
                elementList.get(0).replace(newElement);
            }
        });
    }

    /**
     * elementの存在するスコープを探す
     *
     * @param element 対象のPsiElement
     * @return elementの存在するスコープ
     */
    public static PsiCodeBlock findCodeBlockBelongsTo(@NotNull PsiElement element) {
        PsiElement parentElement = element.getParent();
        if (parentElement instanceof PsiCodeBlock) {
            return (PsiCodeBlock) parentElement;
        }
        return findCodeBlockBelongsTo(parentElement);
    }

    public static PsiMethod findMethodBelongsTo(@NotNull PsiElement element) {
        PsiElement parentElement = element.getParent();
        if (parentElement instanceof PsiMethod) {
            return (PsiMethod) parentElement;
        }
        return findMethodBelongsTo(parentElement);
    }
}
