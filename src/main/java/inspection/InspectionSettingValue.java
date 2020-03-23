package inspection;

public class InspectionSettingValue {
  public static final InspectionSettingValue DEFAULT_NUM_PARAMETER_LIST = new InspectionSettingValue(5);
  public static final InspectionSettingValue DEFAULT_NUM_STATEMENTS = new InspectionSettingValue(30);
  public static final InspectionSettingValue DEFAULT_NUM_CHAINS = new InspectionSettingValue(2);

  private int myDefaultValue;

  private InspectionSettingValue(int defaultValue) {
    myDefaultValue = defaultValue;
  }

  int getValue() {
    return myDefaultValue;
  }
}
