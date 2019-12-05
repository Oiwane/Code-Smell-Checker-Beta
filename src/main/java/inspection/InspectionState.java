package inspection;

public class InspectionState {
  public static final InspectionState LONG_METHOD_INSPECTION_STATE_PROPERTIES_COMPONENT_NAME = new InspectionState("is valid LongMethodInspection");
  public static final InspectionState LONG_PARAMETER_LIST_INSPECTION_STATE_PROPERTIES_COMPONENT_NAME = new InspectionState("is valid LongParameterInspection");
  public static final InspectionState MESSAGE_CHAINS_INSPECTION_STATE_PROPERTIES_COMPONENT_NAME = new InspectionState("is valid MessageChainsInspection");

  private String myInspectionStateName;

  private InspectionState(String name) {
    myInspectionStateName = name;
  }

  public String getName() {
    return myInspectionStateName;
  }
}
