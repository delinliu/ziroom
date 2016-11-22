package database;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DatabaseInterface {

    /**
     * Get all the rooms from database (without history items).
     * The related house and prices and locations will be put into every room and return.
     */
    void getAllRooms(Map<String, RoomEntity> roomMap, Map<String, HouseEntity> houseMap)
            throws InterruptedException, SQLException;

    /**
     * Just update the room's end time. 
     */
    void updateRoomEndTime(RoomEntity roomEntity) throws InterruptedException, SQLException;

    /**
     * Move the room (include the house and prices and locations) into history.
     * Delete the room, the house, the prices and the locations.
     * Insert the room, the house, the prices and the locations with roomEntity, therefore the auto_increment id will be refresh.
     */
    void moveRoomToHistoryWithNoHouseChange(RoomEntity roomEntity) throws InterruptedException, SQLException;

    /**
     * [rooms] are in one same house.
     * The common property [House] is changed.
     * 
     * Move the house and all these rooms, prices and locations into history.
     * Delete the rooms, the house, the prices and the locations.
     * Insert the rooms, the house, the prices and the locations.
     */
    void moveRoomToHistoryWithHouseChange(List<RoomEntity> rooms) throws InterruptedException, SQLException;

    /**
     * Database has no information of the room and hosue. Just insert them into database. 
     */
    void addHouseAndRoom(RoomEntity roomEntity) throws InterruptedException, SQLException;

    /**
     * Database has information of the house but no information of the room.
     * Just insert the room into database.
     */
    void addRoom(RoomEntity roomEntity) throws InterruptedException, SQLException;

    /**
     * Find all room ids of the house. 
     */
    Set<String> getRoomIds(String houseId) throws InterruptedException, SQLException;
}
