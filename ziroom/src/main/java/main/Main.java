package main;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
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
import updater.RoomUpdater;

public class Main {

    public static void main(String[] args) throws Exception {

        String url = "jdbc:mysql://127.0.0.1/ziroom";
        String user = "root";
        String password = "123456";

        String roomListUrl = "http://sh.ziroom.com/z/nl/z2-s6%E5%8F%B7%E7%BA%BF.html"; // line 6
        roomListUrl = "http://sh.ziroom.com/z/nl/z2.html"; // all lines

        Database database = new Database(5, url, user, password);
        Map<String, RoomEntity> roomMap = new HashMap<>();
        Map<String, HouseEntity> houseMap = new HashMap<>();
        database.getAllRooms(roomMap, houseMap);
        ConcurrentSetInterface idSet = new ConcurrentSet();
        RoomListCrawler roomListCrawler = new RoomListCrawler(idSet);
        roomListCrawler.startCrawler(1, 60, 600, roomListUrl, true);
        if (idSet.size() == 0) {
            Thread.sleep(3000);
        }
        BlockingQueue<Room> roomQueue = new ArrayBlockingQueue<>(1000);
        RoomCrawler crawler = new RoomCrawler(idSet, roomQueue);
        crawler.startCrawler(3, 1);
        RoomUpdater updater = new RoomUpdater(roomQueue, database, roomMap, houseMap);
        updater.start();

        new Thread() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    try {
                        String cmd = scanner.nextLine();
                        if (cmd.matches("room crawler sleep second=[0-9]+")) {
                            crawler.setSleepSecond(Integer.parseInt(cmd.split("=")[1]));
                        } else if (cmd.matches("room list crawler sleep second=[0-9]+")) {
                            roomListCrawler.setSleepSecond(Integer.parseInt(cmd.split("=")[1]));
                        } else if (cmd.matches("room list crawler reset second=[0-9]+")) {
                            roomListCrawler.setResetSecond(Integer.parseInt(cmd.split("=")[1]));
                        } else if (cmd.matches("room list crawler current page=[0-9]+")) {
                            roomListCrawler.setCurrentPage(Integer.parseInt(cmd.split("=")[1]));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        // After 5 hours, add the database id to idSet.
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5 * 3600 * 1000); // 5 hours
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                idSet.addAll(roomMap.keySet());
            }
        }.start();
    }
}
