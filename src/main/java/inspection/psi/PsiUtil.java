package inspection.psi;

import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * PSI要素の操作に関するクラス
 */
public class PsiUtil {

  public static PsiMethod cloneMethod(@NotNull PsiMethod originalMethod) {
    PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(originalMethod.getProject());
    PsiMethod newMethod;
    if (originalMethod.isConstructor()) {
      newMethod = factory.createConstructor(originalMethod.getName(), originalMethod.getContext());
    } else {
      newMethod = factory.createMethod(originalMethod.getName(), originalMethod.getReturnType(), originalMethod.getContainingClass());
    }
    PsiParameterList originalParameterList = originalMethod.getParameterList();

    // PsiCodeBlockの作成
    PsiCodeBlock codeBlock = cloneCodeBlock(originalMethod, factory, newMethod);
    // PsiParameterListの作成
    PsiParameterList newParameterList = cloneParameterList(originalParameterList, factory);

    newMethod.getModifierList().replace(originalMethod.getModifierList());
    if (!originalMethod.isConstructor())
      newMethod.getReturnTypeElement().replace(originalMethod.getReturnTypeElement());
    newMethod.getParameterList().replace(newParameterList);
    newMethod.getThrowsList().replace(originalMethod.getThrowsList());
    newMethod.getBody().replace(codeBlock);

    return newMethod;
  }

  @NotNull
  private static PsiCodeBlock cloneCodeBlock(@NotNull PsiMethod originalMethod, @NotNull PsiElementFactory factory, PsiMethod newMethod) {
    String bodyStr = originalMethod.getBody().getText();
    return factory.createCodeBlockFromText(bodyStr, newMethod);
  }

  @NotNull
  private static PsiParameterList cloneParameterList(@NotNull PsiParameterList originalParameterList, PsiElementFactory factory) {
    String[] newParametersName = new String[originalParameterList.getParametersCount()];
    List<PsiType> newType = new ArrayList<>();
    for (int i = 0; i < originalParameterList.getParametersCount(); i++) {
      newParametersName[i] = originalParameterList.getParameters()[i].getName();
      newType.add(i, originalParameterList.getParameters()[i].getType());
    }
    return factory.createParameterList(newParametersName, newType.toArray(new PsiType[0]));
  }

  @NotNull
  public static PsiMethodCallExpression clonePsiMethodCallExpression(@NotNull PsiMethodCallExpression originalElement) {
    PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(originalElement.getProject());
    return (PsiMethodCallExpression) factory.createExpressionFromText(originalElement.getText(), null);
  }

  /**
   * 同じクラス内に同じメソッドがないかを確かめる
   *
   * @param target 検査対象のメソッド
   * @param samples 検査対象のメソッドと同じクラスにあるメソッド
   * @return 同じメソッドがあった場合、trueを返す
   */
  public static boolean existsSameMethod(PsiMethod target, @NotNull PsiMethod[] samples) {
    for (PsiMethod sample : samples) {
      if (!target.getName().equals(sample.getName())) continue;
      if (target.getParameterList().getParametersCount() != sample.getParameterList().getParametersCount()) continue;

      PsiParameter[] targetParameters = target.getParameterList().getParameters();
      PsiParameter[] sampleParameters = sample.getParameterList().getParameters();

      if (isSameParameters(targetParameters, sampleParameters)) return true;
    }

    return false;
  }

  private static boolean isSameParameters(@NotNull PsiParameter[] targetParameters, PsiParameter[] sampleParameters) {
    int counter = 0;
    boolean[] flags = new boolean[targetParameters.length];
    for (int i = 0; i < flags.length; i++) {
      flags[i] = false;
    }
    for (PsiParameter targetParameter : targetParameters) {
      for (int j = 0; j < sampleParameters.length; j++) {
        if (targetParameter.getType().equals(sampleParameters[j].getType()) && !flags[j]) {
          flags[j] = true;
          counter++;
          break;
        }
      }
    }

    return counter == targetParameters.length;
  }

  public static boolean existsSameMethodInOtherNewMethod(@NotNull List<PsiMethod> methodForCompare, PsiMethod newMethod) {
    for (PsiMethod comparedMethod : methodForCompare) {
      if (comparedMethod.getText().equals(newMethod.getText())) return true;
    }

    return false;
  }

  public static void deleteUnusedMethod(@NotNull PsiClass psiClass, String targetMethodName) {
    PsiMethod[] methods = psiClass.getMethods();
    for (PsiMethod method : methods) {
      if (!targetMethodName.equals(method.getName())) continue;

      PsiReference[] references = ReferencesSearch.search(method).toArray(new PsiReference[0]);
      if (references.length != 0) continue;

      method.delete();
    }
  }
}
