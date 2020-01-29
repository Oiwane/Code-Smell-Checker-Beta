package inspection.refactoring.replaceParameterWithMethod;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.refactoring.extractMethod.ExtractMethodHandler;
import com.intellij.refactoring.extractMethod.ExtractMethodProcessor;
import inspection.psi.PsiUtil;
import inspection.refactoring.RefactoringUtil;
import inspection.visitor.LocalVariableVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReplaceParameterWithMethod implements LocalQuickFix {
  private Map<Integer, List<PsiElement>> map;
  private SmartPsiElementPointer<PsiMethod> newMethod;
  private PsiExpression[] arguments;

  @Nls(capitalization = Nls.Capitalization.Sentence)
  @NotNull
  @Override
  public String getFamilyName() {
    return "Replace parameter with method";
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    ApplicationManager.getApplication().invokeLater(() -> {
      CommandProcessor.getInstance().executeCommand(project, () -> {
        PsiMethod method = (PsiMethod) descriptor.getPsiElement().getParent();

        PsiClass psiClass = method.getContainingClass();
        assert psiClass != null;
        List<PsiMethod> methodForCompare = new ArrayList<>();

        PsiReference[] referenceResults = MethodReferencesSearch.search(method).toArray(new PsiReference[0]);
        for (PsiReference referenceResult : referenceResults) {
          newMethod = SmartPointerManager.createPointer(PsiUtil.cloneMethod(method));
          PsiMethod extractedMethod = RefactoringUtil.findMethodBelongsTo(referenceResult.getElement());
          // 呼び出し先のクラスと呼び出されたメソッドのクラスが異なる場合、リファクタリングしない
          if (!psiClass.equals(extractedMethod.getContainingClass())) continue;

          map = new HashMap<>();
          extractElements(referenceResult);
          if (arguments == null) continue;

          createNewMethod(method);

          if (PsiUtil.existsSameMethod(newMethod.getElement(), psiClass.getAllMethods()) ||
                  PsiUtil.existsSameMethodInOtherNewMethod(methodForCompare, newMethod.getElement())) continue;

          WriteCommandAction.runWriteCommandAction(project, () -> {
            psiClass.add(newMethod.getElement());
            LocalVariableVisitor visitor = new LocalVariableVisitor();
            psiClass.accept(visitor);
            for (PsiElement element : visitor.getLocalVariableList()) {
              if (ReferencesSearch.search(element).toArray(new PsiReference[0]).length == 0) {
                element.delete();
              }
            }
            methodForCompare.add(newMethod.getElement());
            PsiDocumentManager.getInstance(project).commitAllDocuments();
          });
        }

        WriteCommandAction.runWriteCommandAction(project, () -> PsiUtil.deleteUnusedMethod(psiClass, method.getName()));
      }, "replace parameter with method", getFamilyName());
    });
  }

  private void extractElements(PsiReference referenceResult) {
    // 普通のメソッドの時
    if (isPsiReferenceExpression(referenceResult)) {
      PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) ((PsiReferenceExpression) referenceResult).getParent();
      arguments = methodCallExpression.getArgumentList().getExpressions();

      extractElements((PsiReferenceExpression) referenceResult);
    }
    // コンストラクタの時
    else if (isPsiJavaCodeReferenceElement(referenceResult)) {
      PsiJavaCodeReferenceElement temp = (PsiJavaCodeReferenceElement) referenceResult;
      PsiNewExpression psiNewExpression = (PsiNewExpression) temp.getParent();
      arguments = psiNewExpression.getArgumentList().getExpressions();

      extractElements(psiNewExpression);
    }
  }

  private void extractElements(@NotNull PsiReferenceExpression referenceExpression) {
    PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) referenceExpression.getParent();
    PsiCodeBlock scope = RefactoringUtil.findCodeBlockInParents(referenceExpression);
    for (PsiStatement statement : scope.getStatements()) {
      if (statement.equals(findStatementInParents(methodCallExpression))) break;
      putExtractedElement(scope, statement);
    }
  }

  private void extractElements(@NotNull PsiNewExpression psiNewExpression) {
    PsiCodeBlock scope = RefactoringUtil.findCodeBlockInParents(psiNewExpression);
    for (PsiStatement statement : scope.getStatements()) {
      if (statement.equals(findStatementInParents(psiNewExpression))) break;
      putExtractedElement(scope, statement);
    }
  }

  private PsiStatement findStatementInParents(@NotNull PsiElement element) {
    PsiElement parentElement = element.getParent();
    if (parentElement instanceof PsiStatement) return (PsiStatement) parentElement;
    else return findStatementInParents(parentElement);
  }

  private void putExtractedElement(PsiCodeBlock scope, PsiStatement statement) {
    for (int i = 0; i < arguments.length; i++) {
      PsiExpression argument = arguments[i];
      if (isPsiReferenceExpression(argument)) {
        if (existsTargetElement(statement, argument)) {
          if (canExtractStatement(scope, statement)) putToMap(i, statement);
        }
      } else if (isPsiMethodCallExpression(argument)) {
        if (canExtractStatement(scope, argument)) putToMap(i, argument);
      }
    }
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

  // TODO この条件式が正しいかを確かめる
  private boolean canExtractStatement(PsiCodeBlock codeBlock, @NotNull PsiElement element) {
    for (PsiElement child : element.getChildren()) {
      if (!canExtractStatement(codeBlock, child)) return false;
      if (!fulfillExtractCondition(codeBlock, child)) return false;
    }

    return true;
  }

  private boolean fulfillExtractCondition(@NotNull PsiCodeBlock codeBlock, @NotNull PsiElement element) {
    if (!(element instanceof PsiReferenceExpression)) return true;

    PsiElement declareElement = element.getReference().resolve();
    if (declareElement == null) return true;
    if (!codeBlock.getTextRange().contains(declareElement.getTextRange())) {
      if (declareElement.getContainingFile().equals(codeBlock.getContainingFile())) {
        return isPsiMethod(declareElement) || isPsiField(declareElement);
      }
    }

    return true;
  }

  private void putToMap(int index, PsiElement element) {
    if (!map.containsKey(index)) map.put(index, new ArrayList<>());
    for (PsiElement sample : map.get(index)) {
      if (sample.equals(element)) return;
    }
    map.get(index).add(element);
  }

  private void createNewMethod(@NotNull PsiMethod originalMethod) {
    Project project = originalMethod.getProject();
    List<Integer> deleteArgumentIndexList = new ArrayList<>();

    for (int key : map.keySet()) {
      List<PsiElement> extractedElementList = map.get(key);
      final PsiParameter targetParameter = newMethod.getElement().getParameterList().getParameters()[key];

      if (extractedElementList.size() == 1) {
        PsiElement element = extractedElementList.get(0);
        if (isPsiMethodCallExpression(element) || isPsiNewExpression(element)) {
          if (isPsiMethodCallExpression(element)) {
            PsiReferenceExpression baseElement = PsiUtil.findBaseElement((PsiMethodCallExpression) element);
            if (baseElement.getReference() != null && isPsiLocalVariable(baseElement.getReference().resolve())) continue;
          }
          replaceParameter(targetParameter, (PsiExpression) element, key, deleteArgumentIndexList);
        } else if (isPsiDeclarationStatement(element)){
          PsiDeclarationStatement declarationStatement = (PsiDeclarationStatement) element;

          if (declarationStatement.getDeclaredElements().length != 1) continue;
          PsiElement child = declarationStatement.getDeclaredElements()[0];
          if (!isPsiVariable(child)) continue;

          final PsiExpression initializer = ((PsiVariable) child).getInitializer();
          final PsiReference reference = initializer.getReference();
          if ((isPsiMethodCallExpression(initializer) && isPsiNewExpression(initializer)) ||
                  (reference != null && isPsiField(reference.resolve()))) {
            replaceParameter(targetParameter, PsiUtil.clonePsiExpression(initializer), key, deleteArgumentIndexList);
          }
        }/* else if (isPsiReferenceExpression(element)) {
          if (element.getReference() != null && isPsiField(element.getReference().resolve())) {
            replaceParameter(targetParameter, (PsiExpression) element, key, deleteArgumentIndexList);
          }
        }*/
      } else {
        PsiElement[] elements = extractedElementList.toArray(new PsiElement[0]);
        ExtractMethodProcessor processor = ExtractMethodHandler.getProcessor(project, elements, originalMethod.getContainingFile(), false);
        assert processor != null;

        if (ExtractMethodHandler.invokeOnElements(project, processor, originalMethod.getContainingFile(), true) ||
                PsiUtil.existsSameMethod(processor.getExtractedMethod(), originalMethod.getContainingClass().getAllMethods())) {
          replaceParameter(targetParameter, processor.getMethodCall(), key, deleteArgumentIndexList);
        }
      }
    }

    changeParameterList(project, deleteArgumentIndexList);
  }

  private void replaceParameter(PsiParameter targetParameter, PsiExpression newElement, int key, @NotNull List<Integer> deleteArgumentIndexList) {
    RefactoringUtil.replaceParameterObject(newMethod.getElement(), targetParameter, newElement);
    WriteCommandAction.runWriteCommandAction(targetParameter.getProject(), () -> arguments[key].delete());
    deleteArgumentIndexList.add(key);
  }

  private void changeParameterList(Project project, List<Integer> deleteArgumentIndexList) {
    WriteCommandAction.runWriteCommandAction(project, () -> {
      PsiParameterList newParameterList = PsiUtil.clonePsiParameterList(project, newMethod.getElement().getParameterList(), deleteArgumentIndexList);
      newMethod.getElement().getParameterList().replace(newParameterList);
    });
  }

  private boolean isPsiField(PsiElement element) {
    return element instanceof PsiField;
  }

  private boolean isPsiMethod(PsiElement element) {
    return element instanceof PsiMethod;
  }

  private boolean isPsiReferenceExpression(Object element) {
    return element instanceof PsiReferenceExpression;
  }

  private boolean isPsiMethodCallExpression(PsiElement element) {
    return element instanceof PsiMethodCallExpression;
  }

  private boolean isPsiDeclarationStatement(PsiElement element) {
    return element instanceof PsiDeclarationStatement;
  }

  private boolean isPsiVariable(PsiElement element) {
    return element instanceof PsiVariable;
  }

  private boolean isPsiNewExpression(PsiElement element) {
    return element instanceof PsiNewExpression;
  }

  private boolean isPsiJavaCodeReferenceElement(PsiReference referenceResult) {
    return referenceResult instanceof PsiJavaCodeReferenceElement;
  }

  private boolean isPsiLocalVariable(PsiElement element) {
    return element instanceof PsiLocalVariable;
  }
}
