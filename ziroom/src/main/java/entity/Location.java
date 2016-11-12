package entity;

public class Location implements Comparable<Location> {

    // 地铁线
    private int line;

    // 地铁站名称
    private String stationName;

    // 距离地铁站的距离
    private int distance;

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(Location o) {
        return distance < o.distance ? -1 : (distance > o.distance ? 1 : 0);
    }
}
