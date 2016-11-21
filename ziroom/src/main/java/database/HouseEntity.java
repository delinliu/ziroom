package database;

import entity.House;

public class HouseEntity {

    private House house;
    private int houseIdLocal;

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public int getHouseIdLocal() {
        return houseIdLocal;
    }

    public void setHouseIdLocal(int houseIdLocal) {
        this.houseIdLocal = houseIdLocal;
    }

}
