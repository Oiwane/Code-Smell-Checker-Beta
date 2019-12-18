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

  String getComponentName() {
    return componentName.getName();
  }

  int getComponentValue() {
    return componentValue.getValue();
  }
}
