package updater;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.Test;

import concurrent_set.ConcurrentSet;
import concurrent_set.ConcurrentSetInterface;
import crawler.RoomCrawler;
import database.Database;
import database.HouseEntity;
import database.RoomEntity;
import entity.Room;
import util.Util;

public class RoomUpdaterTest {

    String url = "jdbc:mysql://127.0.0.1/ziroom_test";
    String user = "root";
    String password = "123456";
    String roomListUrl = "http://sh.ziroom.com/z/nl/z2-s6%E5%8F%B7%E7%BA%BF.html";

    @Test
    public void test() throws Exception {
        Database database = new Database(5, url, user, password);
        Util.clearDatabase(database);
        Map<String, RoomEntity> roomMap = new HashMap<>();
        Map<String, HouseEntity> houseMap = new HashMap<>();
        database.getAllRooms(roomMap, houseMap);
        ConcurrentSetInterface idSet = new ConcurrentSet();
        idSet.addAll(Arrays.asList(new String[] { "60126378", "60280613", "60298387", "60298388" }));
        BlockingQueue<Room> roomQueue = new ArrayBlockingQueue<>(1000);
        RoomCrawler crawler = new RoomCrawler(idSet, roomQueue);
        crawler.startCrawler(1, 1);
        RoomUpdater updater = new RoomUpdater(roomQueue, database, roomMap, houseMap);
        updater.start();
        Thread.sleep(3000000);
    }
}
