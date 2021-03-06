package inspection.codeSmell;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiBlockStatement;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiStatement;
import inspection.CodeSmellInspection;
import inspection.InspectionData;
import refactoring.decomposeConditional.DecomposeConditional;
import refactoring.replaceTempWithQuery.ReplaceTempWithQuery;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.util.ArrayList;
import java.util.List;

/**
 * コードスメル『Long Method（長いメソッド）』のインスペクション
 */
public class LongMethodInspection extends CodeSmellInspection {
    private LocalQuickFix replaceTempWithQuery = new ReplaceTempWithQuery();
    private LocalQuickFix decomposeConditional = new DecomposeConditional();

    public LongMethodInspection() {
        setInspectionData(InspectionData.getInstance(InspectionData.InspectionDataKey.LONG_METHOD));
    }

    @Override
    @NotNull
    public String getDisplayName() {
        return "Long method";
    }

    @NonNls
    @NotNull
    public String getShortName() {
        return "LongMethodInspection";
    }

    @Override
    public JComponent createOptionsPanel() {
        String description = "detected length of \"" + getDisplayName() + "\"";
        return this.createOptionUI(description, getInspectionData());
    }

    @Override
    @Nullable
    public ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
        int count = countStatement(method);
        if (count <= getUpperLimitValue()) {
            return null;
        }

        final PsiIdentifier identifier = method.getNameIdentifier();
        if (identifier == null) {
            return null;
        }

        String descriptionTemplate = getDisplayName() + " : number of statement in method is " + count;
        List<ProblemDescriptor> descriptors = new ArrayList<>();
        descriptors.add(manager.createProblemDescriptor(
                identifier, identifier, descriptionTemplate, ProblemHighlightType.WARNING, isOnTheFly,
                replaceTempWithQuery, decomposeConditional
        ));

        return descriptors.toArray(new ProblemDescriptor[0]);
    }

    private int countStatement(@NotNull PsiMethod method) {
        if (method.getBody() == null) {
            return 0;
        }

        return countStatement(method.getBody());
    }

    private int countStatement(@NotNull PsiCodeBlock codeBlock) {
        int count = 0;

        for (PsiStatement statement : codeBlock.getStatements()) {
            count += countStatement(statement) + countStatementInStatement(statement);
        }

        return count + codeBlock.getStatementCount();
    }

    private int countStatement(@NotNull PsiElement parentElement) {
        int count = 0;

        for (PsiElement element : parentElement.getChildren()) {
            if (element instanceof PsiCodeBlock) {
                count += countStatement((PsiCodeBlock) element);
            } else {
                count += countStatement(element);
            }
        }

        return count;
    }

    private int countStatementInStatement(@NotNull PsiStatement statement) {
        int count = 0;

        for (PsiElement element : statement.getChildren()) {
            if (element instanceof PsiStatement && !(element instanceof PsiBlockStatement)) {
                count += countStatementInStatement((PsiStatement) element) + 1;
            }
        }

        return count;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {

            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);

                addDescriptors(checkMethod(method, holder.getManager(), isOnTheFly));
            }

            private void addDescriptors(final ProblemDescriptor[] descriptors) {
                if (descriptors != null) {
                    for (ProblemDescriptor descriptor : descriptors) {
                        holder.registerProblem(descriptor);
                    }
                }
            }
        };
    }
}