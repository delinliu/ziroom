package entity;

import java.util.List;

public class Room {

    private String roomId;

    // 房间编号（如：05卧）
    private String number;

    // 房间面积
    private int area;

    // 房间朝向
    private String orientation;

    // 风格
    private Style style;

    // 是否有独立阳台
    private boolean separateBalcony;

    // 是否有独立卫生间
    private boolean separateBathroom;

    // 套间信息
    private House house;

    // 房间状态
    private State state;

    // 价格
    private List<Price> prices;

    @Override
    public boolean equals(Object roomObj) {
        if (roomObj == null) {
            return false;
        }
        if (!(roomObj instanceof Room)) {
            return false;
        }
        Room room = (Room) roomObj;
        if (!roomId.equals(room.roomId) || !number.equals(room.number) || area != room.area
                || !orientation.equals(room.orientation) || !style.equals(room.style)
                || separateBalcony != room.separateBalcony || separateBathroom != room.separateBathroom
                || state != room.state) {
            return false;
        }
        if (prices.size() != room.prices.size()) {
            return false;
        }
        for (int i = 0; i < prices.size(); i++) {
            if (!prices.get(i).equals(room.prices.get(i))) {
                return false;
            }
        }
        return true;

    }

    @Override
    public String toString() {
        return "[" + roomId + "] [" + house.getDetailName() + "-" + number + "] ["
                + house.getLocations().get(0).getLine() + "号线-" + house.getLocations().get(0).getStationName() + "-"
                + house.getLocations().get(0).getDistance() + "米] [" + prices.get(0).getRentPerMonth() + "¥] ["
                + area / 100 + "." + area % 100 + "㎡]";
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public boolean isSeparateBalcony() {
        return separateBalcony;
    }

    public void setSeparateBalcony(boolean separateBalcony) {
        this.separateBalcony = separateBalcony;
    }

    public boolean isSeparateBathroom() {
        return separateBathroom;
    }

    public void setSeparateBathroom(boolean separateBathroom) {
        this.separateBathroom = separateBathroom;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public List<Price> getPrices() {
        return prices;
    }

    public void setPrices(List<Price> prices) {
        this.prices = prices;
    }

}
