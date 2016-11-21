package database;

import java.sql.SQLException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class DatabaseTest {

    @Test
    public void test() throws ClassNotFoundException, InterruptedException, SQLException {
        Database database = new Database(1, "jdbc:mysql://127.0.0.1/ziroom_test", "root", "123456");
        Map<String, RoomEntity> roomMap = database.getAllRooms();
        Assert.assertTrue(!roomMap.isEmpty());
        System.out.println(roomMap);
    }
}
