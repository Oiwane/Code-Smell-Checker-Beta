package pass;

import java.util.Calendar;

public class Person {
  private final String name;
  private final int yearOfBirthDay;
  private final int monthOfBirthDay;
  private final int dateOfBirthDay;

  public Person(String name, int yearOfBirthDay, int monthOfBirthDay, int dateOfBirthDay) {
    this.name = name;
    this.yearOfBirthDay = yearOfBirthDay;
    this.monthOfBirthDay = monthOfBirthDay;
    this.dateOfBirthDay = dateOfBirthDay;
  }

  String getName() {
    return name;
  }

  int getYearOfBirthDay() {
    return yearOfBirthDay;
  }

  int getMonthOfBirthDay() {
    return monthOfBirthDay;
  }

  int getDateOfBirthDay() {
    return dateOfBirthDay;
  }
}
