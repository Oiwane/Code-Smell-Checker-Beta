import pass.CommuterPass;
import pass.Person;

public class Main {
  public static void main(String[] args) {
    CommuterPass senior = new CommuterPass(new Person("Alice", 2020 - 66, 11, 13));
    CommuterPass junior = new CommuterPass(new Person("Bob", 2012, 3, 5), 3000);
    CommuterPass adult = new CommuterPass(new Person("Carol", 1993, 6, 8));

    senior.ride(1);
    junior.ride(1);

    senior.getOff(10);
    adult.ride(10);

    junior.getOff(15);
    adult.getOff(15);

    junior.ride(15);
    junior.getOff(10);

    junior.ride(10);
    junior.getOff(1);
  }
}
