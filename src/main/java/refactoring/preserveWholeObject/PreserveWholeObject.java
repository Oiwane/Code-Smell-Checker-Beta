package refactoring.preserveWholeObject;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import com.intellij.psi.search.searches.ReferencesSearch;
import psi.PsiUtil;
import refactoring.RefactoringUtil;
import visitor.LocalVariableVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreserveWholeObject implements LocalQuickFix {
    private Map<PsiElement, List<ArgumentInfo>> map;
    public static final String QUICK_FIX_NAME = "Preserve Whole Object";

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return QUICK_FIX_NAME;
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        ApplicationManager.getApplication().invokeLater(() -> {
            CommandProcessor.getInstance().executeCommand(project, () -> {
                PsiParameterList parameterList = (PsiParameterList) descriptor.getPsiElement();
                PsiMethod method = (PsiMethod) parameterList.getParent();
                PsiClass psiClass = method.getContainingClass();
                assert psiClass != null;
                List<PsiMethod> methodForCompare = new ArrayList<>();

                PsiReference[] referenceResults = MethodReferencesSearch.search(method).toArray(new PsiReference[0]);
                for (PsiReference referenceResult : referenceResults) {
                    if (!(referenceResult.getElement().getParent() instanceof PsiMethodCallExpression)) continue;
                    PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) referenceResult.getElement().getParent();

                    map = new HashMap<>();

                    extractArgumentInfo(methodCallExpression);
                    PsiMethod newMethod = PsiUtil.cloneMethod(method);
                    createParameterList(newMethod);

                    // メソッドの呼び出し先の編集
                    changeArgumentList(methodCallExpression);

                    if (PsiUtil.existsSameMethod(newMethod, psiClass.getAllMethods()) ||
                            PsiUtil.existsSameMethodInOtherNewMethod(methodForCompare, newMethod)) continue;

                    WriteCommandAction.runWriteCommandAction(project, () -> {
                        psiClass.add(newMethod);

                        LocalVariableVisitor visitor = new LocalVariableVisitor();
                        psiClass.accept(visitor);
                        for (PsiElement element : visitor.getLocalVariableList()) {
                            if (ReferencesSearch.search(element).toArray(new PsiReference[0]).length == 0) {
                                element.delete();
                            }
                        }

                        methodForCompare.add(newMethod);
                        PsiDocumentManager.getInstance(project).commitAllDocuments();
                    });
                }

                WriteCommandAction.runWriteCommandAction(project, () -> {
                    PsiUtil.deleteUnusedMethod(psiClass, method.getName());
                });
            }, "preserve whole object", getFamilyName());
        });
    }

    private void extractArgumentInfo(@NotNull PsiMethodCallExpression methodCallExpression) {
        PsiExpression[] arguments = methodCallExpression.getArgumentList().getExpressions();

        for (int i = 0; i < arguments.length; i++) {
            PsiExpression argument = arguments[i];
            // 引数がメソッド呼び出しになっているかを確認する
            if (argument instanceof PsiMethodCallExpression) {
                addArgumentInfo(map, i, argument);
            }
            // TODO オブジェクトがどこで宣言されたのかを確認する
            // TODO 制約 : 同じスコープ内の変数（オブジェクト）しか見ない
            else if (argument instanceof PsiReferenceExpression) {
                PsiCodeBlock scope = RefactoringUtil.findCodeBlockInParents(methodCallExpression);
                PsiMethodCallExpression argumentMethod = findTargetElementInScope(scope, argument);
                if (argumentMethod != null) addArgumentInfo(map, i, argumentMethod);
            }
        }
    }

    private void addArgumentInfo(@NotNull Map<PsiElement, List<ArgumentInfo>> map, int index, PsiExpression argumentMethod) {
        PsiElement key = extractObjectCallingMethod(argumentMethod).getReference().resolve();

        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<>());
        }
        map.get(key).add(new ArgumentInfo(index, ((PsiMethodCallExpression) argumentMethod)));
    }

    /**
     * オブジェクトを特定する
     *
     * @param expression PsiExpression
     * @return 最終的にはPsiReferenceExpressionが戻ってくる
     */
    @Nullable
    private PsiExpression extractObjectCallingMethod(@NotNull PsiExpression expression) {
        for (PsiElement childElement : expression.getChildren()) {
            if (childElement instanceof PsiExpression) {
                return extractObjectCallingMethod((PsiExpression) childElement);
            }
        }

        return expression;
    }

    @Nullable
    private PsiMethodCallExpression findTargetElementInScope(@NotNull PsiCodeBlock scope, PsiElement targetElement) {
        PsiMethodCallExpression returnValue = null;
        for (PsiStatement statement : scope.getStatements()) {
            if (statement instanceof PsiDeclarationStatement) {
                returnValue = findTargetElementInStatement(scope, (PsiDeclarationStatement) statement, targetElement);
            } else if (statement instanceof PsiExpressionStatement) {
                returnValue = findTargetElementInStatement(scope, (PsiExpressionStatement) statement, targetElement);
            }
            if (returnValue != null) return returnValue;
        }

        return null;
    }

    @Nullable
    private PsiMethodCallExpression findTargetElementInStatement(PsiCodeBlock scope, @NotNull PsiDeclarationStatement statement, @NotNull PsiElement targetElement) {
        PsiMethodCallExpression returnValue = null;
        final PsiReference reference = targetElement.getReference();
        if (reference == null) return null;

        // int a = b = c.hoge(); みたいな記述は無視
        for (PsiElement element : statement.getDeclaredElements()) {
            if (element instanceof PsiLocalVariable) {
                PsiLocalVariable localVariable = (PsiLocalVariable) element;
                final PsiExpression initializer = localVariable.getInitializer();

                if (initializer instanceof PsiMethodCallExpression) {
                    returnValue = (PsiMethodCallExpression) initializer;
                } else if (initializer instanceof PsiReferenceExpression) {
                    PsiElement variableReference = initializer.getReference().resolve();
                    return findTargetElementInScope(scope, variableReference);
                }
            }
        }

        return (statement.getTextRange().equals(reference.resolve().getTextRange())) ? returnValue : null;
    }

    @Nullable
    private PsiMethodCallExpression findTargetElementInStatement(PsiCodeBlock scope, @NotNull PsiExpressionStatement statement, PsiElement targetElement) {
        PsiExpression expression = statement.getExpression();
        PsiReferenceExpression compareVariable = null;
        PsiMethodCallExpression returnValue = null;
        boolean appearEQ = false;
        for (PsiElement element : expression.getChildren()) {
            if (element instanceof PsiReferenceExpression) {
                if (appearEQ) {
                    return findTargetElementInScope(scope, element);
                } else {
                    compareVariable = (PsiReferenceExpression) element;
                }
            } else if (element instanceof PsiMethodCallExpression) {
                returnValue = (PsiMethodCallExpression) element;
            } else if (element instanceof PsiJavaToken) {
                PsiJavaToken javaToken = (PsiJavaToken) element;
                appearEQ = javaToken.getTokenType().equals(JavaTokenType.EQ);
            }
        }

        if (compareVariable == null) return null;
        assert compareVariable.getReference() != null;
        if (targetElement.getReference() == null) return null;
        return (compareVariable.getReference().resolve().equals(targetElement.getReference().resolve())) ? returnValue : null;
    }

    private void createParameterList(@NotNull PsiMethod newMethod) {
        PsiParameterList newParameterList = newMethod.getParameterList();
        for (PsiElement key : map.keySet()) {
            if (map.get(key).size() < 2) continue;

            addParameter(map, key, newParameterList);

            List<PsiParameter> parameters = new ArrayList<>();
            for (ArgumentInfo argumentInfo : map.get(key)) {
                PsiParameter targetParameter = newParameterList.getParameters()[argumentInfo.getIndex()];
                parameters.add(targetParameter);
            }

            ArgumentInfo[] argumentInfo = map.get(key).toArray(new ArgumentInfo[0]);
            for (int i = 0; i < argumentInfo.length; i++) {
                RefactoringUtil.optimiseParameter(newMethod, parameters.get(i), argumentInfo[i].getArgumentMethod());
            }
        }
    }

    /**
     * パラメータを追加する
     *
     * @param map              追加するパラメータの対象となるオブジェクトを格納しているマップ
     * @param key              mapの添え字
     * @param newParameterList 新しいパラメータリスト
     * @brief パラメータの一番最後に任意のオブジェクト用のパラメータを追加する
     */
    private void addParameter(@NotNull Map<PsiElement, List<ArgumentInfo>> map, PsiElement key, @NotNull PsiParameterList newParameterList) {
        PsiElementFactory factory = PsiElementFactory.getInstance(newParameterList.getProject());
        if (map.containsKey(key)) {
            if (!(key instanceof PsiVariable)) return;
            PsiVariable variable = (PsiVariable) key;
            PsiParameter newParameter = factory.createParameter(variable.getName(), variable.getType());

            newParameterList.add(newParameter);
        }
    }

    private void changeArgumentList(@NotNull PsiMethodCallExpression methodCallExpression) {
        PsiExpressionList argumentList = methodCallExpression.getArgumentList();
        PsiExpression[] arguments = argumentList.getExpressions();
        final Project project = methodCallExpression.getProject();

        for (PsiElement key : map.keySet()) {
            if (map.get(key).size() < 2) continue;

            PsiElementFactory factory = PsiElementFactory.getInstance(project);
            if (map.containsKey(key)) {
                if (!(key instanceof PsiVariable)) return;
                PsiVariable variable = (PsiVariable) key;
                PsiExpression newArgument = factory.createExpressionFromText(variable.getNameIdentifier().getText(), argumentList);
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    argumentList.add(newArgument);
                });
            }

            List<PsiExpression> targetArguments = new ArrayList<>();
            for (ArgumentInfo argumentInfo : map.get(key)) {
                PsiExpression targetArgument = arguments[argumentInfo.getIndex()];
                targetArguments.add(targetArgument);
            }

            WriteCommandAction.runWriteCommandAction(project, () -> {
                for (PsiExpression deleteArgument : targetArguments) {
                    deleteArgument.delete();
                }
            });
        }
    }

}
