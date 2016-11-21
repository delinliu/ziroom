package database;

import java.util.Date;

import entity.Room;

public class RoomEntity {

    private Room room;
    private HouseEntity houseEntity;
    private int roomIdLocal;
    private int houseIdLocal;
    private Date begin;
    private Date end;
    private Date newEnd = null;

    private boolean isTimeChanged = false;
    private boolean isRoomChanged = false;
    private boolean isHouseChanged = false;

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }


    public HouseEntity getHouseEntity() {
        return houseEntity;
    }

    public void setHouseEntity(HouseEntity houseEntity) {
        this.houseEntity = houseEntity;
    }

    public int getRoomIdLocal() {
        return roomIdLocal;
    }

    public void setRoomIdLocal(int roomIdLocal) {
        this.roomIdLocal = roomIdLocal;
    }

    public int getHouseIdLocal() {
        return houseIdLocal;
    }

    public void setHouseIdLocal(int houseIdLocal) {
        this.houseIdLocal = houseIdLocal;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getNewEnd() {
        return newEnd;
    }

    public void setNewEnd(Date newEnd) {
        this.newEnd = newEnd;
    }

    public boolean isTimeChanged() {
        return isTimeChanged;
    }

    public void setTimeChanged(boolean isTimeChanged) {
        this.isTimeChanged = isTimeChanged;
    }

    public boolean isRoomChanged() {
        return isRoomChanged;
    }

    public void setRoomChanged(boolean isRoomChanged) {
        this.isRoomChanged = isRoomChanged;
    }

    public boolean isHouseChanged() {
        return isHouseChanged;
    }

    public void setHouseChanged(boolean isHouseChanged) {
        this.isHouseChanged = isHouseChanged;
    }

    @Override
    public String toString() {
        return room.toString();
    }

}
