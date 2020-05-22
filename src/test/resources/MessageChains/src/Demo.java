public class Demo {
    private Person person;

    public void run() {
        person = new Person("Alice", 31);
        person.addChild(new Person("Bob", 11));
        person.addChild(new Person("Carol", 8));
        person.addChild(new Person("Dave", 4));

        for (int index = 0; index < person.getNumOfChildren(); index++) {
            person.getChildren().get(index).showInfo();
        }
    }

}
