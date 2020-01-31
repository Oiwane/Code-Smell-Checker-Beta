package busStop;

public class BusStop {
  private String name;
  private boolean canUseCommuterPass;

  BusStop(String name, boolean canUseCommuterPass) {
    this.name = name;
    this.canUseCommuterPass = canUseCommuterPass;
  }

  public String getName() {
    return name;
  }

  public boolean canUseCommuterPass() {
    return canUseCommuterPass;
  }
}
