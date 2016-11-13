package entity;

import java.util.List;

public class House {

    private String houseId;

    // 房间名称（如：云山星座苑4居室）
    private String detailName;

    // 小区位置（如：[浦东 金杨] 6号线 云山路）
    private String notDetailName;

    // 户型（如：3室2厅）
    private String layout;

    // 卧室数
    private int bedroom;

    // 客厅数
    private int livingroom;

    // 楼层
    private int currentFloor;

    // 总层数
    private int totalFloor;

    // 距离地铁站的距离
    private List<Location> locations;

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

    public int getBedroom() {
        return bedroom;
    }

    public void setBedroom(int bedroom) {
        this.bedroom = bedroom;
    }

    public int getLivingroom() {
        return livingroom;
    }

    public void setLivingroom(int livingroom) {
        this.livingroom = livingroom;
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

}
