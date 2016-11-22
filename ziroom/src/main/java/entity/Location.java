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
    public boolean equals(Object locationObj) {
        if (locationObj == null) {
            return false;
        }
        if (!(locationObj instanceof Location)) {
            return false;
        }
        Location location = (Location) locationObj;
        if (line != location.line || distance != location.distance || !stationName.equals(location.stationName)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Location o) {
        if (distance < o.distance) {
            return -1;
        }
        if (distance > o.distance) {
            return 1;
        }
        if (line < o.line) {
            return -1;
        }
        if (line > o.line) {
            return 1;
        }
        if (stationName.hashCode() < o.stationName.hashCode()) {
            return -1;
        }
        if (stationName.hashCode() > o.stationName.hashCode()) {
            return 1;
        }
        return 0;
    }
}
