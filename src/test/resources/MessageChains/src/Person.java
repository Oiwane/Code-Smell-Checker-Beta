import java.util.ArrayList;
import java.util.List;

public class Person {
  private List<Person> children;
  private String name;
  private int age;

  public Person(String name, int age) {
    children = new ArrayList<>();
    this.name = name;
    this.age = age;
  }

  public void addChild(Person child) {
    children.add(child);
  }

  public List<Person> getChildren() {
    return children;
  }

  public void showInfo() {
    System.out.println("name : " + name);
    System.out.println("age : " + age);
  }

  public int getNumOfChildren() {
    return children.size();
  }
}
