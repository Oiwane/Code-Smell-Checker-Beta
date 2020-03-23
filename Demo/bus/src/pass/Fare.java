package pass;

class Fare {
  static int calculate(int startBusStopId, int endBusStopId) {
    return 100 + Math.abs(endBusStopId - startBusStopId) * 50;
  }
}
