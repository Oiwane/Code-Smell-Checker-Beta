package inspection.refactoring.replaceParameterWithMethod;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import com.intellij.refactoring.extractMethod.ExtractMethodHandler;
import com.intellij.refactoring.extractMethod.ExtractMethodProcessor;
import inspection.refactoring.RefactoringUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * このリファクタリングはメソッドのアクセス修飾子がprivateの時のみ使用できるようにした
 */
public class ReplaceParameterWithMethod implements LocalQuickFix {
  private Project myProject;
  private PsiMethod method;
  private PsiParameterList parameterList;

  @Nls(capitalization = Nls.Capitalization.Sentence)
  @NotNull
  @Override
  public String getFamilyName() {
    return "Replace parameter with method";
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    myProject = project;
    parameterList = (PsiParameterList) descriptor.getPsiElement();
    method = (PsiMethod) parameterList.getParent();

    PsiReference[] results = MethodReferencesSearch.search(method).toArray(new PsiReference[0]);

    List<List<List<PsiStatement>>> extractedStatementList = new ArrayList<>();
    RefactoringUtil<PsiStatement> refactoringUtil = new RefactoringUtil<>();
    refactoringUtil.initThreeDimensionalList(extractedStatementList, results.length, parameterList.getParametersCount());

    extractStatements(results, extractedStatementList);

    // TODO : 同じパラメータに該当する部分で抽出できない場合が一回でもあったら抽出しないようにする
    refactor(extractedStatementList);
  }

  /**
   * 抽出候補のPsiStatementをリストに保存する
   *
   * @param results メソッドを呼び出している場所
   * @param extractedStatements 抽出するPsiStatementを保存するリスト
   */
  private void extractStatements(@NotNull PsiReference[] results, @NotNull List<List<List<PsiStatement>>> extractedStatements) {

    for (int i = 0; i < results.length; i++) {
      if (results[i] instanceof PsiReferenceExpression) {
        List<List<PsiStatement>> extractCandidate = new ArrayList<>();
        for (int j = 0; j < extractedStatements.get(0).size(); j++) {
          extractCandidate.add(new ArrayList<>());
        }
        PsiReferenceExpression referenceExpression = (PsiReferenceExpression) results[i];
        PsiCodeBlock scopeCallingMethod = findCodeBlockInParents(referenceExpression);
        PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) referenceExpression.getParent();

        PsiExpression[] arguments = methodCallExpression.getArgumentList().getExpressions();

        // コードブロックつまり同スコープ以下に引数で使用している変数がないかを確認する
        for (PsiStatement statement : scopeCallingMethod.getStatements()) {
          if (statement.equals(findStatementInParents(methodCallExpression))) break;

          for (int j = 0; j < arguments.length; j++) {
            if (existsTargetElement(statement, arguments[j])) {
              extractCandidate.get(j).add(statement);
            }
          }
        }

        for (int j = 0; j < extractCandidate.size(); j++) {
          for (int k = 0; k < extractCandidate.get(j).size(); k++) {
            PsiStatement statement = extractCandidate.get(j).get(k);
            if (canExtractStatement(scopeCallingMethod, statement)) {
              extractedStatements.get(i).get(j).add(k, statement);
            }
          }
          // TODO : 変数を消すならここら辺で処理をする
        }
      }
    }
  }

  private PsiCodeBlock findCodeBlockInParents(@NotNull PsiElement element) {
    PsiElement parentElement = element.getParent();
    if (parentElement instanceof PsiCodeBlock) return (PsiCodeBlock) parentElement;
    else return findCodeBlockInParents(parentElement);
  }

  private PsiStatement findStatementInParents(@NotNull PsiElement element) {
    PsiElement parentElement = element.getParent();
    if (parentElement instanceof PsiStatement) return (PsiStatement) parentElement;
    else return findStatementInParents(parentElement);
  }

  private boolean existsTargetElement(@NotNull PsiElement element, PsiElement target) {
    for (PsiElement childElement : element.getChildren()) {
      if (existsTargetElement(childElement, target)) return true;
      if (childElement instanceof PsiIdentifier) {
        PsiIdentifier identifier = (PsiIdentifier) childElement;
        if (identifier.getText().equals(target.getText())) return true;
      }
    }

    return false;
  }

  private boolean canExtractStatement(PsiCodeBlock codeBlock, @NotNull PsiElement element) {
    for (PsiElement child : element.getChildren()) {
      if (!canExtractStatement(codeBlock, child)) return false;
      if (!fulfillExtractCondition(codeBlock, child)) return false;
    }

    return true;
  }

  private boolean fulfillExtractCondition(@NotNull PsiCodeBlock codeBlock, @NotNull PsiElement element) {
    if (!(element instanceof PsiReferenceExpression)) return true;

    PsiElement navigationElement = element.getReference().resolve();
    if (navigationElement == null) return true;
    if (!codeBlock.getTextRange().contains(navigationElement.getTextRange())) {
      if (navigationElement.getContainingFile().equals(method.getContainingFile())) {
        return isPsiMethod(navigationElement) || isPsiField(navigationElement);
      }
    }

    return true;
  }

  private boolean isPsiField(PsiElement element) {
    return element instanceof PsiField;
  }

  private boolean isPsiMethod(PsiElement element) {
    return element instanceof PsiMethod;
  }

  private void refactor(@NotNull List<List<List<PsiStatement>>> extractedStatementList) {
    for (List<List<PsiStatement>> listList : extractedStatementList) {
      for (int i = 0; i < listList.size(); i++) {
        PsiElement[] elements = listList.get(i).toArray(new PsiStatement[0]);
        if (elements.length == 0) continue;
        ExtractMethodProcessor processor = ExtractMethodHandler.getProcessor(myProject, elements, method.getContainingFile(), false);
        assert processor != null;

        int finalI = i;
        ApplicationManager.getApplication().invokeLater(() -> {
          if (ExtractMethodHandler.invokeOnElements(myProject, processor, method.getContainingFile(), true)) {
            PsiParameter parameter = parameterList.getParameters()[finalI];
            RefactoringUtil.replaceParameterObject(processor.getMethodCall(), parameter);
            RefactoringUtil.deleteUnnecessaryParameter(parameter);
          }
        });
      }
    }
  }

}
