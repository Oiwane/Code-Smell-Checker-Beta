package inspection;

public class InspectionSettingName {
  public static final InspectionSettingName LONG_METHOD_PROPERTIES_COMPONENT_NAME = new InspectionSettingName("limit value used for LongMethodInspection");
  public static final InspectionSettingName LONG_PARAMETER_LIST_PROPERTIES_COMPONENT_NAME = new InspectionSettingName("limit value used for LongParameterListInspection");
  public static final InspectionSettingName MESSAGE_CHAINS_PROPERTIES_COMPONENT_NAME = new InspectionSettingName("limit value used for MessageChainsInspection");

  private String myPropertiesComponentName;

  private InspectionSettingName(String name) {
    myPropertiesComponentName = name;
  }

  public String getName() {
    return myPropertiesComponentName;
  }
}
