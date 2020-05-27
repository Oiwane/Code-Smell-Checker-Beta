package visitor;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

import java.util.List;

public class TemporaryVariableVisitorTest extends LightJavaCodeInsightFixtureTestCase {

    public void testVisitLocalVariable() {
        myFixture.addClass(
                "public class Item {\n" +
                "    private String name;\n" +
                "    private int originalPrice;\n" +
                "    private boolean hasEatenHere;\n" +
                "\n" +
                "    public Item(String name, int originalPrice, boolean hasEatenHere) {\n" +
                "        this.name = name;\n" +
                "        this.originalPrice = originalPrice;\n" +
                "        this.hasEatenHere = hasEatenHere;\n" +
                "    }\n" +
                "\n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n" +
                "\n" +
                "    public int getOriginalPrice() {\n" +
                "        return originalPrice;\n" +
                "    }\n" +
                "\n" +
                "    public boolean hasEatenHere() {\n" +
                "        return hasEatenHere;\n" +
                "    }\n" +
                "\n" +
                "    public boolean isFood() {\n" +
                "        return this instanceof Food;\n" +
                "    }\n" +
                "\n" +
                "    public boolean isSoftDrink() {\n" +
                "        return this instanceof SoftDrink;\n" +
                "    }\n" +
                "\n" +
                "    public static void demo() {\n" +
                "        List<Item> itemList = new ArrayList<>();\n" +
                "\n" +
                "        itemList.add(new SoftDrink(\"お茶\", 100, true));\n" +
                "        itemList.add(new SoftDrink(\"ジュース\", 100, false));\n" +
                "        itemList.add(new Vegetable(\"玉ねぎ\", 50));\n" +
                "        itemList.add(new Food(\"菓子パン\", 100, true));\n" +
                "        itemList.add(new Food(\"食パン\", 100, false));\n" +
                "        itemList.add(new Alcohol(\"ビール\", 150, false));\n" +
                "        itemList.add(new DairyNecessity(\"シャンプー\", 250));\n" +
                "\n" +
                "        int totalPrice = 0;\n" +
                "        for (Item item : itemList) {\n" +
                "            if (item == null)\n" +
                "                if (item.getOriginalPrice() < 0)\n" +
                "                    continue;\n" +
                "            double tax = item.isFood() && item.hasEatenHere() || item.isSoftDrink() && item.hasEatenHere() || !item.isFood() && !item.isSoftDrink() ? 0.10 : 0.08;\n" +
                "            final int price = (int) Math.floor(item.getOriginalPrice() * (1.0 + tax));\n" +
                "            totalPrice += price;\n" +
                "            System.out.println(item.getName() + \" : \" + price);\n" +
                "        }\n" +
                "\n" +
                "        System.out.println(\"------------------------\");\n" +
                "        System.out.println(\"小計 : \" + totalPrice);\n" +
                "    }\n" +
                "}\n"
        );
        PsiClass psiClass = myFixture.findClass("Item");
        PsiMethod[] methods = psiClass.findMethodsByName("demo", false);
        assertEquals(1, methods.length);
        TemporaryVariableVisitor visitor = new TemporaryVariableVisitor();
        methods[0].getBody().accept(visitor);
        List<PsiElement> tempVariableList = visitor.getTempVariableList();
        assertEquals(1, tempVariableList.size());
        assertEquals(
                "double tax = item.isFood() && item.hasEatenHere() || " +
                "item.isSoftDrink() && item.hasEatenHere() || !item.isFood() && " +
                "!item.isSoftDrink() ? 0.10 : 0.08;", tempVariableList.get(0).getText());
    }

    public void testGetTempVariableList() {
        // getterは上記のテストで確認
    }
}