package database;

import java.sql.SQLException;
import java.util.Map;

public interface DatabaseInterface {

    Map<String, RoomEntity> getAllRooms() throws InterruptedException, SQLException;
}
