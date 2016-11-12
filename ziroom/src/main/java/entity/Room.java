package entity;

public class Room {

    private String roomId;

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

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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

}
