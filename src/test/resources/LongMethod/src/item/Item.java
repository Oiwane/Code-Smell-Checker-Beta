package item;

import java.util.ArrayList;
import java.util.List;

public class Item {
  private String name;
  private int originalPrice;
  private boolean hasEatenHere;

  public Item(String name, int originalPrice, boolean hasEatenHere) {
    this.name = name;
    this.originalPrice = originalPrice;
    this.hasEatenHere = hasEatenHere;
  }

  public String getName() {
    return name;
  }

  public int getOriginalPrice() {
    return originalPrice;
  }

  public boolean hasEatenHere() {
    return hasEatenHere;
  }

  public boolean isFood() {
    return this instanceof Food;
  }

  public boolean isSoftDrink() {
    return this instanceof SoftDrink;
  }

  public static void demo() {
    List<Item> itemList = new ArrayList<>();

    itemList.add(new SoftDrink("お茶", 100, true));
    itemList.add(new SoftDrink("ジュース", 100, false));
    itemList.add(new Vegetable("玉ねぎ", 50));
    itemList.add(new Food("菓子パン", 100, true));
    itemList.add(new Food("食パン", 100, false));
    itemList.add(new Alcohol("ビール", 150, false));
    itemList.add(new DairyNecessity("シャンプー", 250));

    int totalPrice = 0;
    for (Item item : itemList) {
      double tax = item.isFood() && item.hasEatenHere() || item.isSoftDrink() && item.hasEatenHere() || !item.isFood() && !item.isSoftDrink() ? 0.10 : 0.08;
      final int price = (int) Math.floor(item.getOriginalPrice() * (1.0 + tax));
      totalPrice += price;
      System.out.println(item.getName() + " : " + price);
    }

    System.out.println("------------------------");
    System.out.println("小計 : " + totalPrice);
  }
}
