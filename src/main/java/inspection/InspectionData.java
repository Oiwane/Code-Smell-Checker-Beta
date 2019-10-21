package inspection;

/**
 * インスペクションの検出条件に関するデータクラス
 */
public class InspectionData {
  private String componentName;
  private int componentValue;

  public InspectionData(String componentName, int componentValue) {
    this.componentName = componentName;
    this.componentValue = componentValue;
  }

  public String getComponentName() {
    return componentName;
  }

  public int getComponentValue() {
    return componentValue;
  }
}
