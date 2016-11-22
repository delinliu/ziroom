package updater;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.Assert;
import org.junit.Test;

import database.Database;
import database.HouseEntity;
import database.RoomEntity;
import entity.Room;
import parser.RoomParser;
import util.Util;

public class RoomUpdaterTest {

    String url = "jdbc:mysql://127.0.0.1/ziroom_test";
    String user = "root";
    String password = "123456";

    final static String availableRoomPath = "src/test/resource/simulator/available-room.html";
    final static String availableRoomPath2 = "src/test/resource/simulator/available-room2.html";
    final static String availableRoomPath3 = "src/test/resource/simulator/available-room3.html";
    final static String roomId1 = "60300024";
    final static String roomId2 = "60300025";
    final static String roomId3 = "60300023";
    final static String houseId = "60049455";

    @Test
    public void test() throws Exception {

        // They have one same house.
        String content1 = Util.readFile(availableRoomPath);
        String content2 = Util.readFile(availableRoomPath2);
        String content3 = Util.readFile(availableRoomPath3);

        Database database = new Database(5, url, user, password);
        Util.clearDatabase(database);
        Map<String, RoomEntity> roomMap = new HashMap<>();
        Map<String, HouseEntity> houseMap = new HashMap<>();
        database.getAllRooms(roomMap, houseMap);
        BlockingQueue<Room> roomQueue = new ArrayBlockingQueue<>(1000);
        RoomUpdater updater = new RoomUpdater(roomQueue, database, roomMap, houseMap);
        updater.start();

        RoomEntity room1, room2, room3;
        HouseEntity house;
        int room1IdLocalOld, room2IdLocalOld;
        int houseIdLocalOld;
        int room1IdLocalNew, room2IdLocalNew;
        int houseIdLocalNew;

        long sleepTime = 2000;
        RoomParser parser = new RoomParser();
        Room room = parser.parseRoom(content1);
        roomQueue.put(room); // new house, new room
        Thread.sleep(sleepTime);
        Assert.assertEquals(1, roomMap.size());
        Assert.assertEquals(1, houseMap.size());
        Assert.assertNotNull(roomMap.get(roomId1));
        Assert.assertNotNull(houseMap.get(houseId));
        room1 = roomMap.get(roomId1);
        house = houseMap.get(houseId);
        assertRoomAndHouseEntity(room1, house);
        Assert.assertEquals(room1.getBegin(), room1.getEnd());
        room1IdLocalOld = room1.getRoomIdLocal();
        houseIdLocalOld = room1.getHouseIdLocal();

        room = parser.parseRoom(content1);
        roomQueue.put(room); // old house, old room, nothing changed
        Thread.sleep(sleepTime);
        Assert.assertEquals(1, roomMap.size());
        Assert.assertEquals(1, houseMap.size());
        Assert.assertNotNull(roomMap.get(roomId1));
        Assert.assertNotNull(houseMap.get(houseId));
        room1 = roomMap.get(roomId1);
        house = houseMap.get(houseId);
        assertRoomAndHouseEntity(room1, house);
        Assert.assertNotEquals(room1.getBegin(), room1.getEnd());
        room1IdLocalNew = room1.getRoomIdLocal();
        houseIdLocalNew = room1.getHouseIdLocal();
        Assert.assertEquals(room1IdLocalOld, room1IdLocalNew);
        Assert.assertEquals(houseIdLocalOld, houseIdLocalNew);
        room1IdLocalOld = room1IdLocalNew;
        houseIdLocalOld = houseIdLocalNew;

        room = parser.parseRoom(content1);
        room.setArea(1);
        roomQueue.put(room); // old house, old room, room changed
        Thread.sleep(sleepTime);
        Assert.assertEquals(1, roomMap.size());
        Assert.assertEquals(1, houseMap.size());
        Assert.assertNotNull(roomMap.get(roomId1));
        Assert.assertNotNull(houseMap.get(houseId));
        room1 = roomMap.get(roomId1);
        house = houseMap.get(houseId);
        Assert.assertEquals(1, room1.getRoom().getArea());
        assertRoomAndHouseEntity(room1, house);
        Assert.assertEquals(room1.getBegin(), room1.getEnd());
        room1IdLocalNew = room1.getRoomIdLocal();
        houseIdLocalNew = room1.getHouseIdLocal();
        Assert.assertNotEquals(room1IdLocalOld, room1IdLocalNew);
        Assert.assertNotEquals(houseIdLocalOld, houseIdLocalNew);
        room1IdLocalOld = room1IdLocalNew;
        houseIdLocalOld = houseIdLocalNew;

        room = parser.parseRoom(content1);
        room.setArea(1);
        room.getHouse().setBedroom(100);
        roomQueue.put(room); // old house, old room, house changed
        Thread.sleep(sleepTime);
        Assert.assertEquals(1, roomMap.size());
        Assert.assertEquals(1, houseMap.size());
        Assert.assertNotNull(roomMap.get(roomId1));
        Assert.assertNotNull(houseMap.get(houseId));
        room1 = roomMap.get(roomId1);
        house = houseMap.get(houseId);
        Assert.assertEquals(1, room1.getRoom().getArea());
        Assert.assertEquals(100, house.getHouse().getBedroom());
        assertRoomAndHouseEntity(room1, house);
        Assert.assertEquals(room1.getBegin(), room1.getEnd());
        room1IdLocalNew = room1.getRoomIdLocal();
        houseIdLocalNew = room1.getHouseIdLocal();
        Assert.assertNotEquals(room1IdLocalOld, room1IdLocalNew);
        Assert.assertNotEquals(houseIdLocalOld, houseIdLocalNew);
        room1IdLocalOld = room1IdLocalNew;
        houseIdLocalOld = houseIdLocalNew;

        room = parser.parseRoom(content1);
        room.setArea(2);
        room.getHouse().setBedroom(50);
        room.getPrices().get(0).setRentPerMonth(100);
        roomQueue.put(room); // old house, old room, house and room changed
        Thread.sleep(sleepTime);
        Assert.assertEquals(1, roomMap.size());
        Assert.assertEquals(1, houseMap.size());
        Assert.assertNotNull(roomMap.get(roomId1));
        Assert.assertNotNull(houseMap.get(houseId));
        room1 = roomMap.get(roomId1);
        house = houseMap.get(houseId);
        Assert.assertEquals(2, room1.getRoom().getArea());
        Assert.assertEquals(50, house.getHouse().getBedroom());
        Assert.assertEquals(100, room1.getRoom().getPrices().get(0).getRentPerMonth());
        assertRoomAndHouseEntity(room1, house);
        Assert.assertEquals(room1.getBegin(), room1.getEnd());
        room1IdLocalNew = room1.getRoomIdLocal();
        houseIdLocalNew = room1.getHouseIdLocal();
        Assert.assertNotEquals(room1IdLocalOld, room1IdLocalNew);
        Assert.assertNotEquals(houseIdLocalOld, houseIdLocalNew);
        room1IdLocalOld = room1IdLocalNew;
        houseIdLocalOld = houseIdLocalNew;

        room = parser.parseRoom(content2);
        room.getHouse().setBedroom(50);
        roomQueue.put(room); // old house, new room, nothing changed
        Thread.sleep(sleepTime);
        Assert.assertEquals(2, roomMap.size());
        Assert.assertEquals(1, houseMap.size());
        Assert.assertNotNull(roomMap.get(roomId1));
        Assert.assertNotNull(roomMap.get(roomId2));
        Assert.assertNotNull(houseMap.get(houseId));
        room1 = roomMap.get(roomId1);
        room2 = roomMap.get(roomId2);
        house = houseMap.get(houseId);
        Assert.assertEquals(2, room1.getRoom().getArea());
        Assert.assertEquals(50, house.getHouse().getBedroom());
        Assert.assertEquals(100, room1.getRoom().getPrices().get(0).getRentPerMonth());
        assertRoomAndHouseEntity(room1, house);
        assertRoomAndHouseEntity(room2, house);
        Assert.assertEquals(room1.getBegin(), room1.getEnd());
        Assert.assertEquals(room2.getBegin(), room2.getEnd());
        room1IdLocalNew = room1.getRoomIdLocal();
        room2IdLocalNew = room2.getRoomIdLocal();
        houseIdLocalNew = room1.getHouseIdLocal();
        Assert.assertEquals(room1IdLocalOld, room1IdLocalNew);
        Assert.assertEquals(houseIdLocalOld, houseIdLocalNew);
        room1IdLocalOld = room1IdLocalNew;
        room2IdLocalOld = room2IdLocalNew;
        houseIdLocalOld = houseIdLocalNew;

        room = parser.parseRoom(content2);
        room.getHouse().setBedroom(50);
        room.getPrices().get(0).setRentPerMonth(200);
        roomQueue.put(room); // old house, old room, room changed
        Thread.sleep(sleepTime);
        Assert.assertEquals(2, roomMap.size());
        Assert.assertEquals(1, houseMap.size());
        Assert.assertNotNull(roomMap.get(roomId1));
        Assert.assertNotNull(roomMap.get(roomId2));
        Assert.assertNotNull(houseMap.get(houseId));
        room1 = roomMap.get(roomId1);
        room2 = roomMap.get(roomId2);
        house = houseMap.get(houseId);
        Assert.assertEquals(2, room1.getRoom().getArea());
        Assert.assertEquals(50, house.getHouse().getBedroom());
        Assert.assertEquals(100, room1.getRoom().getPrices().get(0).getRentPerMonth());
        Assert.assertEquals(200, room2.getRoom().getPrices().get(0).getRentPerMonth());
        assertRoomAndHouseEntity(room1, house);
        assertRoomAndHouseEntity(room2, house);
        Assert.assertEquals(room1.getBegin(), room1.getEnd());
        Assert.assertEquals(room2.getBegin(), room2.getEnd());
        room1IdLocalNew = room1.getRoomIdLocal();
        room2IdLocalNew = room2.getRoomIdLocal();
        houseIdLocalNew = room1.getHouseIdLocal();
        Assert.assertEquals(room1IdLocalOld, room1IdLocalNew);
        Assert.assertNotEquals(room2IdLocalOld, room2IdLocalNew);
        Assert.assertNotEquals(houseIdLocalOld, houseIdLocalNew);
        room1IdLocalOld = room1IdLocalNew;
        room2IdLocalOld = room2IdLocalNew;
        houseIdLocalOld = houseIdLocalNew;

        room = parser.parseRoom(content2);
        room.getHouse().setBedroom(50);
        room.getPrices().get(0).setRentPerMonth(200);
        roomQueue.put(room); // old house, old room, nothing changed
        Thread.sleep(sleepTime);
        Assert.assertEquals(2, roomMap.size());
        Assert.assertEquals(1, houseMap.size());
        Assert.assertNotNull(roomMap.get(roomId1));
        Assert.assertNotNull(roomMap.get(roomId2));
        Assert.assertNotNull(houseMap.get(houseId));
        room1 = roomMap.get(roomId1);
        room2 = roomMap.get(roomId2);
        house = houseMap.get(houseId);
        Assert.assertEquals(2, room1.getRoom().getArea());
        Assert.assertEquals(50, house.getHouse().getBedroom());
        Assert.assertEquals(100, room1.getRoom().getPrices().get(0).getRentPerMonth());
        Assert.assertEquals(200, room2.getRoom().getPrices().get(0).getRentPerMonth());
        assertRoomAndHouseEntity(room1, house);
        assertRoomAndHouseEntity(room2, house);
        Assert.assertEquals(room1.getBegin(), room1.getEnd());
        Assert.assertNotEquals(room2.getBegin(), room2.getEnd());
        room1IdLocalNew = room1.getRoomIdLocal();
        room2IdLocalNew = room2.getRoomIdLocal();
        houseIdLocalNew = room1.getHouseIdLocal();
        Assert.assertEquals(room1IdLocalOld, room1IdLocalNew);
        Assert.assertEquals(room2IdLocalOld, room2IdLocalNew);
        Assert.assertEquals(houseIdLocalOld, houseIdLocalNew);
        room1IdLocalOld = room1IdLocalNew;
        room2IdLocalOld = room2IdLocalNew;
        houseIdLocalOld = houseIdLocalNew;

        room = parser.parseRoom(content2);
        room.getPrices().get(0).setRentPerMonth(200);
        room.getHouse().setBedroom(200);
        roomQueue.put(room); // old house, old room, house changed
        Thread.sleep(sleepTime);
        Assert.assertEquals(2, roomMap.size());
        Assert.assertEquals(1, houseMap.size());
        Assert.assertNotNull(roomMap.get(roomId1));
        Assert.assertNotNull(roomMap.get(roomId2));
        Assert.assertNotNull(houseMap.get(houseId));
        room1 = roomMap.get(roomId1);
        room2 = roomMap.get(roomId2);
        house = houseMap.get(houseId);
        Assert.assertEquals(2, room1.getRoom().getArea());
        Assert.assertEquals(200, house.getHouse().getBedroom());
        Assert.assertEquals(100, room1.getRoom().getPrices().get(0).getRentPerMonth());
        Assert.assertEquals(200, room2.getRoom().getPrices().get(0).getRentPerMonth());
        assertRoomAndHouseEntity(room1, house);
        assertRoomAndHouseEntity(room2, house);
        Assert.assertEquals(room1.getBegin(), room1.getEnd());
        Assert.assertEquals(room2.getBegin(), room2.getEnd());
        room1IdLocalNew = room1.getRoomIdLocal();
        room2IdLocalNew = room2.getRoomIdLocal();
        houseIdLocalNew = room1.getHouseIdLocal();
        Assert.assertNotEquals(room1IdLocalOld, room1IdLocalNew);
        Assert.assertNotEquals(room2IdLocalOld, room2IdLocalNew);
        Assert.assertNotEquals(houseIdLocalOld, houseIdLocalNew);
        room1IdLocalOld = room1IdLocalNew;
        room2IdLocalOld = room2IdLocalNew;
        houseIdLocalOld = houseIdLocalNew;

        room = parser.parseRoom(content2);
        room.getPrices().get(0).setRentPerMonth(150);
        room.getHouse().setBedroom(150);
        roomQueue.put(room); // old house, old room, house and room changed
        Thread.sleep(sleepTime);
        Assert.assertEquals(2, roomMap.size());
        Assert.assertEquals(1, houseMap.size());
        Assert.assertNotNull(roomMap.get(roomId1));
        Assert.assertNotNull(roomMap.get(roomId2));
        Assert.assertNotNull(houseMap.get(houseId));
        room1 = roomMap.get(roomId1);
        room2 = roomMap.get(roomId2);
        house = houseMap.get(houseId);
        Assert.assertEquals(2, room1.getRoom().getArea());
        Assert.assertEquals(150, house.getHouse().getBedroom());
        Assert.assertEquals(100, room1.getRoom().getPrices().get(0).getRentPerMonth());
        Assert.assertEquals(150, room2.getRoom().getPrices().get(0).getRentPerMonth());
        assertRoomAndHouseEntity(room1, house);
        assertRoomAndHouseEntity(room2, house);
        Assert.assertEquals(room1.getBegin(), room1.getEnd());
        Assert.assertEquals(room2.getBegin(), room2.getEnd());
        room1IdLocalNew = room1.getRoomIdLocal();
        room2IdLocalNew = room2.getRoomIdLocal();
        houseIdLocalNew = room1.getHouseIdLocal();
        Assert.assertNotEquals(room1IdLocalOld, room1IdLocalNew);
        Assert.assertNotEquals(room2IdLocalOld, room2IdLocalNew);
        Assert.assertNotEquals(houseIdLocalOld, houseIdLocalNew);
        room1IdLocalOld = room1IdLocalNew;
        room2IdLocalOld = room2IdLocalNew;
        houseIdLocalOld = houseIdLocalNew;

        room = parser.parseRoom(content3);
        room.getHouse().setBedroom(125);
        room.getHouse().setCurrentFloor(100);
        roomQueue.put(room); // old house, new room, house changed
        Thread.sleep(sleepTime);
        Assert.assertEquals(3, roomMap.size());
        Assert.assertEquals(1, houseMap.size());
        Assert.assertNotNull(roomMap.get(roomId1));
        Assert.assertNotNull(roomMap.get(roomId2));
        Assert.assertNotNull(roomMap.get(roomId3));
        Assert.assertNotNull(houseMap.get(houseId));
        room1 = roomMap.get(roomId1);
        room2 = roomMap.get(roomId2);
        room3 = roomMap.get(roomId3);
        house = houseMap.get(houseId);
        Assert.assertEquals(2, room1.getRoom().getArea());
        Assert.assertEquals(125, house.getHouse().getBedroom());
        Assert.assertEquals(100, house.getHouse().getCurrentFloor());
        Assert.assertEquals(100, room1.getRoom().getPrices().get(0).getRentPerMonth());
        Assert.assertEquals(150, room2.getRoom().getPrices().get(0).getRentPerMonth());
        assertRoomAndHouseEntity(room1, house);
        assertRoomAndHouseEntity(room2, house);
        assertRoomAndHouseEntity(room3, house);
        Assert.assertEquals(room1.getBegin(), room1.getEnd());
        Assert.assertEquals(room2.getBegin(), room2.getEnd());
        Assert.assertEquals(room3.getBegin(), room3.getEnd());
        room1IdLocalNew = room1.getRoomIdLocal();
        room2IdLocalNew = room2.getRoomIdLocal();
        houseIdLocalNew = room1.getHouseIdLocal();
        Assert.assertNotEquals(room1IdLocalOld, room1IdLocalNew);
        Assert.assertNotEquals(room2IdLocalOld, room2IdLocalNew);
        Assert.assertNotEquals(houseIdLocalOld, houseIdLocalNew);
    }

    private void assertRoomAndHouseEntity(RoomEntity roomEntity, HouseEntity houseEntity) {
        Assert.assertSame(roomEntity.getHouseEntity(), houseEntity);
        Assert.assertSame(roomEntity.getRoom().getHouse(), houseEntity.getHouse());
        Assert.assertEquals(roomEntity.getHouseIdLocal(), houseEntity.getHouseIdLocal());
    }
}
