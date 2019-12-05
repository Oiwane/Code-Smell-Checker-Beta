package inspection;

/**
 * インスペクションの検出条件に関するデータクラス
 */
public class InspectionData {
  private InspectionSettingName componentName;
  private InspectionSettingValue componentValue;

  public InspectionData(InspectionSettingName componentName, InspectionSettingValue componentValue) {
    this.componentName = componentName;
    this.componentValue = componentValue;
  }

  public String getComponentName() {
    return componentName.getName();
  }

  public int getComponentValue() {
    return componentValue.getValue();
  }
}
