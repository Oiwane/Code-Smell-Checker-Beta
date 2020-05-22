package item;

public class ItemType {
    public static final ItemType FOOD = new ItemType("food");
    public static final ItemType ALCOHOL = new ItemType("Alcohol");

    private String name;

    private ItemType(String name) {
        this.name = name;
    }
}
