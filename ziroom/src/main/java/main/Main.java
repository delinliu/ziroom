package main;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import concurrent_set.ConcurrentSet;
import concurrent_set.ConcurrentSetInterface;
import crawler.RoomCrawler;
import crawler.RoomListCrawler;
import database.Database;
import database.HouseEntity;
import database.RoomEntity;
import entity.Room;

public class Main {

    public static void main(String[] args) throws Exception {

        String url = "jdbc:mysql://127.0.0.1/ziroom";
        String user = "root";
        String password = "123456";

        String roomListUrl = "http://sh.ziroom.com/z/nl/z2-s6%E5%8F%B7%E7%BA%BF.html";

        Database database = new Database(5, url, user, password);
        Map<String, RoomEntity> roomMap = new HashMap<>();
        Map<String, HouseEntity> houseMap = new HashMap<>();
        database.getAllRooms(roomMap, houseMap);
        ConcurrentSetInterface idSet = new ConcurrentSet();
        idSet.addAll(roomMap.keySet());
        RoomListCrawler roomListCrawler = new RoomListCrawler(idSet);
        roomListCrawler.startCrawler(1, 1, 5, roomListUrl);
        if (idSet.size() == 0) {
            Thread.sleep(3000);
        }
        BlockingQueue<Room> roomQueue = new ArrayBlockingQueue<>(1000);
        RoomCrawler crawler = new RoomCrawler(idSet, roomQueue);
        crawler.startCrawler(1, 1);
    }
}
