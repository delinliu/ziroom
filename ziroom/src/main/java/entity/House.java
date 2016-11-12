package entity;

import java.util.List;

public class House {

    private String houseId;

    // 房间名称（云山星座苑4居室）
    private String detailName;

    // [浦东 金杨] 6号线 云山路
    private String notDetailName;

    // 户型
    private String layout;

    // 楼层
    private int currentFloor;

    // 总层数
    private int totalFloor;

    // 距离地铁站的距离
    private List<Location> locations;

    // 价格
    private List<Price> prices;

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getDetailName() {
        return detailName;
    }

    public void setDetailName(String detailName) {
        this.detailName = detailName;
    }

    public String getNotDetailName() {
        return notDetailName;
    }

    public void setNotDetailName(String notDetailName) {
        this.notDetailName = notDetailName;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public int getTotalFloor() {
        return totalFloor;
    }

    public void setTotalFloor(int totalFloor) {
        this.totalFloor = totalFloor;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<Price> getPrices() {
        return prices;
    }

    public void setPrices(List<Price> prices) {
        this.prices = prices;
    }

}
