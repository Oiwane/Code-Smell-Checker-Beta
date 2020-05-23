package busStop;

public class BusStopManager {
  private static BusStopManager singleton = new BusStopManager();
  private BusStopMap map;
  private static final String NO_EXIST_ID = "入力されたIDは存在しません。";

  private BusStopManager() {
    map = new BusStopMap();
    add(1, new BusStop("宮崎大学", true));
    add(10, new BusStop("宮崎駅", true));
    add(15, new BusStop("ショッピングモール", false));
  }

  public static BusStopManager getInstance() {
    return singleton;
  }

  private void add(int id, BusStop busStop) {
    map.put(id, busStop);
  }

  public BusStop getBusStop(int id) {
    if (map.containsKey(id)) {
      return map.get(id);
    }
    System.out.println(NO_EXIST_ID);
    return null;
  }

  public int getId(String busStopName) {
    for (int id : map.keySet()) {
      if (map.get(id).getName().equals(busStopName)) {
        return id;
      }
    }

    return -1;
  }

  public void changeID(int currentId, int newId) {
    BusStop busStop = map.get(currentId);

    if (!map.containsKey(currentId)) {
      System.out.println(NO_EXIST_ID);
      return;
    }
    map.put(newId, busStop);
    map.remove(currentId);
  }

}
