package inspection.refactoring.hideDelegate;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.util.TypeConversionUtil;
import com.intellij.refactoring.extractMethod.ExtractMethodProcessor;
import com.intellij.refactoring.extractMethod.InputVariables;
import com.intellij.refactoring.extractMethod.ReusedLocalVariable;
import com.intellij.refactoring.extractMethod.ReusedLocalVariablesFinder;
import com.intellij.refactoring.util.RefactoringUtil;
import com.intellij.refactoring.util.VariableData;
import com.intellij.refactoring.util.duplicates.Match;
import com.intellij.refactoring.util.duplicates.ReturnValue;
import com.intellij.refactoring.util.duplicates.VariableReturnValue;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class HideDelegateExtractMethodProcessor extends ExtractMethodProcessor {
  private PsiReferenceExpression base;
  private PsiElementFactory myElementFactory;
  private static final Logger LOG = Logger.getInstance("#inspection.refactoring.hideDelegate.HideDelegateExtractMethodProcessor");

  HideDelegateExtractMethodProcessor(Project project,
                                     Editor editor,
                                     PsiElement[] elements,
                                     PsiType forcedReturnType,
                                     String refactoringName,
                                     String initialMethodName,
                                     String helpId,
                                     PsiReferenceExpression base) {
    super(project, editor, elements, forcedReturnType, refactoringName, initialMethodName, helpId);
    this.base = base;
    PsiManager myManager = PsiManager.getInstance(myProject);
    myElementFactory = JavaPsiFacade.getElementFactory(myManager.getProject());
  }

  @Override
  public PsiElement processMatch(@NotNull Match match) throws IncorrectOperationException {
    if (PsiTreeUtil.isContextAncestor(myExtractedMethod.getContainingClass(), match.getMatchStart(), false) &&
            RefactoringUtil.isInStaticContext(match.getMatchStart(), myExtractedMethod.getContainingClass())) {
      PsiUtil.setModifierProperty(myExtractedMethod, PsiModifier.STATIC, true);
    }
    final PsiMethodCallExpression methodCallExpression = generateMethodCall(match.getInstanceExpression(), false);

    ArrayList<VariableData> datas = new ArrayList<>();
    for (final VariableData variableData : myVariableDatum) {
      if (variableData.passAsParameter) {
        datas.add(variableData);
      }
    }
    final PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(myProject);
    for (VariableData data : datas) {
      final List<PsiElement> parameterValue = match.getParameterValues(data.variable);
      if (parameterValue != null) {
        for (PsiElement val : parameterValue) {
          if (val instanceof PsiExpression && isCastRequired(data, (PsiExpression)val)) {
            final PsiTypeCastExpression cast = (PsiTypeCastExpression)elementFactory.createExpressionFromText("(A)a", val);
            cast.getCastType().replace(elementFactory.createTypeElement(data.type));
            cast.getOperand().replace(val.copy());
            val = cast;
          }
          methodCallExpression.getArgumentList().add(val);
        }
      } else {
        methodCallExpression.getArgumentList().add(myElementFactory.createExpressionFromText(data.variable.getName(), methodCallExpression));
      }
    }
    List<String> reusedVariables = findReusedVariables(match, myInputVariables, myOutputVariable);

    // ここからオリジナル
    final String text = base.getText() + "." + methodCallExpression.getText();
    PsiMethodCallExpression newMethodCallExpression = (PsiMethodCallExpression) myElementFactory.createExpressionFromText(text, null);
    // ここまでオリジナル

    PsiElement replacedMatch = match.replace(myExtractedMethod, newMethodCallExpression, myOutputVariable, myReturnType);

    PsiElement appendLocation = addNotNullConditionalCheck(match, replacedMatch);
    declareReusedVariables(appendLocation, reusedVariables);

    return replacedMatch;
  }

  private static boolean isCastRequired(@NotNull VariableData data, @NotNull PsiExpression val) {
    final PsiType exprType = val.getType();
    if (exprType == null || TypeConversionUtil.isAssignable(data.type, exprType)) {
      return false;
    }
    final PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(data.type);
    return !(psiClass instanceof PsiTypeParameter);
  }

  @NotNull
  private static List<String> findReusedVariables(@NotNull Match match,
                                                  @NotNull InputVariables inputVariables,
                                                  @Nullable PsiVariable outputVariable) {
    Set<PsiLocalVariable> ignoreVariables = Collections.emptySet();
    ReturnValue returnValue = match.getOutputVariableValue(outputVariable);
    if (returnValue instanceof VariableReturnValue) {
      PsiVariable returnedVariable = ((VariableReturnValue)returnValue).getVariable();
      if (returnedVariable instanceof PsiLocalVariable) {
        ignoreVariables = Collections.singleton((PsiLocalVariable)returnedVariable);
      }
    }
    List<ReusedLocalVariable> reusedLocalVariables =
            ReusedLocalVariablesFinder.findReusedLocalVariables(match.getMatchStart(), match.getMatchEnd(), ignoreVariables, inputVariables);
    if (!reusedLocalVariables.isEmpty()) {
      List<String> result = new ArrayList<>();
      for (ReusedLocalVariable variable : reusedLocalVariables) {
        if (!variable.reuseValue()) {
          result.add(variable.getDeclarationText());
        }
      }
      return result;
    }

    return Collections.emptyList();
  }

  private PsiElement addNotNullConditionalCheck(Match match, PsiElement replacedMatch) {
    if ((myNotNullConditionalCheck || myGenerateConditionalExit) && myOutputVariable != null) {
      ReturnValue returnValue = match.getOutputVariableValue(myOutputVariable);
      if (returnValue instanceof VariableReturnValue) {
        String varName = ((VariableReturnValue)returnValue).getVariable().getName();
        LOG.assertTrue(varName != null, "returned variable name is null");
        PsiStatement statement = PsiTreeUtil.getParentOfType(replacedMatch, PsiStatement.class, false);
        if (statement != null) {
          PsiStatement conditionalExit = myNotNullConditionalCheck ?
                  generateNotNullConditionalStatement(varName) : generateConditionalExitStatement(varName);
          return statement.getParent().addAfter(conditionalExit, statement);
        }
      }
    }
    return replacedMatch;
  }

  private static void declareReusedVariables(@NotNull PsiElement appendLocation, @NotNull List<String> reusedVariables) {
    if (reusedVariables.isEmpty()) {
      return;
    }
    PsiElementFactory factory = JavaPsiFacade.getElementFactory(appendLocation.getProject());

    for (String variable : reusedVariables) {
      PsiStatement declaration = factory.createStatementFromText(variable, appendLocation);
      appendLocation = appendLocation.getParent().addAfter(declaration, appendLocation);
    }
  }

  private PsiStatement generateNotNullConditionalStatement(String varName) {
    return myElementFactory.createStatementFromText("if (" + varName + " != null) return " + varName + ";", null);
  }

  @NotNull
  private PsiIfStatement generateConditionalExitStatement(String varName) {
    if (myFirstExitStatementCopy instanceof PsiReturnStatement && ((PsiReturnStatement)myFirstExitStatementCopy).getReturnValue() != null) {
      return (PsiIfStatement)myElementFactory.createStatementFromText("if (" + varName + "==null) return null;", null);
    }
    return (PsiIfStatement)myElementFactory.createStatementFromText("if (" + varName + "==null) " + myFirstExitStatementCopy.getText(), null);
  }
}
