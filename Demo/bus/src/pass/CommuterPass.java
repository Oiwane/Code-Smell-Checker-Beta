package pass;

import busStop.BusStopManager;
import input.Input;

import java.util.Calendar;

public class CommuterPass {
  private Person person;
  private int money;
  private int startBusStopId;
  private int endBusStopId;

  private static final int NON = -1;
  private BusStopManager manager;

  public CommuterPass(Person person) {
    this.person = person;
    money = 0;
    manager = BusStopManager.getInstance();
  }

  public CommuterPass(Person person, int money) {
    this.person = person;
    this.money = money;
    manager = BusStopManager.getInstance();
  }

  public void ride(int startBusStopId) {
    System.out.println(person.getName() + "が" + manager.getBusStop(startBusStopId).getName() + "で乗車しました。");
    this.startBusStopId = startBusStopId;
  }

  public void getOff(int endBusStopId) {
    System.out.println(person.getName() + "が" + manager.getBusStop(endBusStopId).getName() + "で下車しました。");
    this.endBusStopId = endBusStopId;
    pay();
    resetBusStop();
  }

  private void resetBusStop() {
    startBusStopId = NON;
    endBusStopId = NON;
  }

  private void charge(int money) {
    this.money += money;
    System.out.println(money + "円チャージしました。");
  }

  private void pay() {
    Input input = Input.getInstance();

    if (!(manager.getBusStop(startBusStopId).canUseCommuterPass() && manager.getBusStop(endBusStopId).canUseCommuterPass())) {

      final Calendar calendar = Calendar.getInstance();
      final int currentYear = calendar.get(Calendar.YEAR);
      final int currentMonth = calendar.get(Calendar.MONTH);
      final int currentDate = calendar.get(Calendar.DATE);

      final int yearOfBirthDay = person.getYearOfBirthDay();
      final int monthOfBirthDay = person.getMonthOfBirthDay();
      final int dateOfBirthDay = person.getDateOfBirthDay();

      final int adjustment;
      if (currentMonth < monthOfBirthDay || (currentMonth == monthOfBirthDay && currentDate < dateOfBirthDay))
        adjustment = 1;
      else adjustment = 0;
      final int age = currentYear - yearOfBirthDay - adjustment;

      while (startBusStopId == NON) {
        System.out.println("乗車したバス停が分かりません。");
        System.out.println("乗車したパス停を入力してください。");
        startBusStopId = manager.getId(input.inputStr());
        if (startBusStopId == NON) {
          System.out.println("存在しないバス停です。");
        }
      }

      final int payedMoney;
      if (6 <= age && age <= 12) {
        payedMoney = Math.floorDiv(Fare.calculate(startBusStopId, endBusStopId), 2);
      } else if (65 <= age) {
        payedMoney = Math.floorDiv(Fare.calculate(startBusStopId, endBusStopId) * 2, 3);
      } else {
        payedMoney = Fare.calculate(startBusStopId, endBusStopId);
      }

      if (money < payedMoney) {
        System.out.println("お金が足りません。チャージする金額を入力してください。");
        charge(input.inputInt());
      }

      money -= payedMoney;
      System.out.println(payedMoney + "円払いました。");
      System.out.println("残高は" + money + "円です。\n");
    }
  }
}
