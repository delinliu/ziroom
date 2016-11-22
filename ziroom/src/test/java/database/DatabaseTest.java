package database;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import entity.House;
import entity.Room;
import entity.State;
import util.Util;

public class DatabaseTest {

    private String roomId = "1000";
    private String roomId2 = "2000";

    private String url = "jdbc:mysql://127.0.0.1/ziroom_test";
    private String user = "root";
    private String password = "123456";

    @Test
    public void testAddRoom() throws Exception {
        Database database = new Database(1, url, user, password);
        Util.initDatabase(database);
        Map<String, RoomEntity> roomMap = new HashMap<>();
        Map<String, HouseEntity> houseMap = new HashMap<>();
        database.getAllRooms(roomMap, houseMap);

        RoomEntity roomEntity = roomMap.get(roomId);
        RoomEntity roomEntity2 = roomMap.get(roomId2);
        Util.clearDatabase(database);
        database.addHouseAndRoom(roomEntity2);
        roomEntity.setHouseIdLocal(-1);
        roomEntity.setRoomIdLocal(-1);
        database.addRoom(roomEntity);
        int roomIdLocal = roomEntity.getRoomIdLocal();
        int houseIdlocal = roomEntity.getHouseIdLocal();
        roomMap.clear();
        houseMap.clear();
        database.getAllRooms(roomMap, houseMap);
        Assert.assertEquals(2, roomMap.size());
        Assert.assertEquals(1, houseMap.size());
        roomEntity = roomMap.get(roomId);
        assertTheRoom(roomEntity.getRoom());
        assertTheHouse(roomEntity.getRoom().getHouse());
        Assert.assertEquals(roomIdLocal, roomEntity.getRoomIdLocal());
        Assert.assertEquals(houseIdlocal, roomEntity.getHouseIdLocal());
    }

    @Test
    public void testGetRoomIds() throws Exception {
        Database database = new Database(1, url, user, password);
        Util.initDatabase(database);
        Set<String> roomIds = database.getRoomIds("1");
        Set<String> expectedIds = new HashSet<>(Arrays.asList(new String[] { roomId, roomId2 }));
        Assert.assertEquals(expectedIds, roomIds);
    }

    @Test
    public void testAddHouseAndRoom() throws Exception {
        Database database = new Database(1, url, user, password);
        Util.initDatabase(database);
        Map<String, RoomEntity> roomMap = new HashMap<>();
        Map<String, HouseEntity> houseMap = new HashMap<>();
        database.getAllRooms(roomMap, houseMap);

        RoomEntity roomEntity = roomMap.get(roomId);
        Util.clearDatabase(database);
        roomEntity.setHouseIdLocal(-1);
        roomEntity.setRoomIdLocal(-1);
        database.addHouseAndRoom(roomEntity);
        int roomIdLocal = roomEntity.getRoomIdLocal();
        int houseIdlocal = roomEntity.getHouseIdLocal();
        roomMap.clear();
        houseMap.clear();
        database.getAllRooms(roomMap, houseMap);
        Assert.assertEquals(1, roomMap.size());
        Assert.assertEquals(1, houseMap.size());
        roomEntity = roomMap.get(roomId);
        assertTheRoom(roomEntity.getRoom());
        assertTheHouse(roomEntity.getRoom().getHouse());
        Assert.assertEquals(roomIdLocal, roomEntity.getRoomIdLocal());
        Assert.assertEquals(houseIdlocal, roomEntity.getHouseIdLocal());
    }

    private void assertTheRoom(Room room) {
        Assert.assertEquals("1000", room.getRoomId());
        Assert.assertEquals("number", room.getNumber());
        Assert.assertEquals(10, room.getArea());
        Assert.assertEquals("南", room.getOrientation());
        Assert.assertEquals("木棉", room.getStyle().getStyle());
        Assert.assertEquals(4, room.getStyle().getVersion());
        Assert.assertEquals(true, room.isSeparateBalcony());
        Assert.assertEquals(false, room.isSeparateBathroom());
        Assert.assertEquals(State.Available, room.getState());
        Assert.assertEquals(4, room.getPrices().size());
    }

    private void assertTheHouse(House house) {
        Assert.assertEquals("1", house.getHouseId());
        Assert.assertEquals("detail name", house.getDetailName());
        Assert.assertEquals("not detail name", house.getNotDetailName());
        Assert.assertEquals("layout", house.getLayout());
        Assert.assertEquals(3, house.getBedroom());
        Assert.assertEquals(1, house.getLivingroom());
        Assert.assertEquals(5, house.getCurrentFloor());
        Assert.assertEquals(10, house.getTotalFloor());
        Assert.assertEquals(3, house.getLocations().size());
    }

    @Test
    public void testGetRoom() throws ClassNotFoundException, InterruptedException, SQLException, NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Database database = new Database(1, url, user, password);
        Util.initDatabase(database);
        Map<String, RoomEntity> roomMap = new HashMap<>();
        Map<String, HouseEntity> houseMap = new HashMap<>();
        database.getAllRooms(roomMap, houseMap);
        Assert.assertEquals(2, roomMap.size());
        RoomEntity roomEntity = roomMap.get(roomId);
        Room room = roomEntity.getRoom();
        Assert.assertEquals(10000, roomEntity.getRoomIdLocal());
        assertTheRoom(room);
        assertTheHouse(room.getHouse());
    }

    @Test
    public void testUpdateEndTime()
            throws ClassNotFoundException, InterruptedException, SQLException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Database database = new Database(1, url, user, password);
        Util.initDatabase(database);
        Map<String, RoomEntity> roomMap = new HashMap<>();
        Map<String, HouseEntity> houseMap = new HashMap<>();
        database.getAllRooms(roomMap, houseMap);
        RoomEntity roomEntity = roomMap.get(roomId);
        Timestamp date = new Timestamp(new Date().getTime());
        roomEntity.setNewEnd(date);
        database.updateRoomEndTime(roomEntity);
        roomMap.clear();
        houseMap.clear();
        database.getAllRooms(roomMap, houseMap);
        Assert.assertTrue(Math.abs(date.getTime() - roomMap.get(roomId).getEnd().getTime()) <= 1000);
    }

    @Test
    public void testMoveWithHouseChange()
            throws SQLException, InterruptedException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Database database = new Database(1, url, user, password);
        Util.initDatabase(database);
        Map<String, RoomEntity> roomMap = new HashMap<>();
        Map<String, HouseEntity> houseMap = new HashMap<>();
        database.getAllRooms(roomMap, houseMap);
        RoomEntity roomEntity = roomMap.get(roomId);
        Assert.assertNotNull(roomEntity);
        Timestamp date = new Timestamp(new Date().getTime());
        Room room = roomEntity.getRoom();
        int newArea = 100;
        String newNumber = "new number";
        String newOrientation = "北";
        boolean newSeparateBalcony = false;
        boolean newSeparateBathroom = true;
        State newState = State.Unavailable;
        int newRentPerMonth = 50;
        String newStyle = "米苏";
        int newStyleVersion = 5;
        room.setArea(newArea);
        room.setNumber(newNumber);
        room.setOrientation(newOrientation);
        room.setSeparateBalcony(newSeparateBalcony);
        room.setSeparateBathroom(newSeparateBathroom);
        room.setState(newState);
        room.getPrices().get(0).setRentPerMonth(newRentPerMonth);
        room.getStyle().setStyle(newStyle);
        room.getStyle().setVersion(newStyleVersion);
        roomEntity.setBegin(date);
        roomEntity.setEnd(date);

        int newBedroom = 10;
        int newCurrentFloor = 100;
        String newDetailName = "dddtailname";
        String newLayout = "llllayout";
        int newLivingroom = 20;
        String newNotDetailName = "nnnnnot detailname";
        int newTotalFloor = 1000;
        int newDistance = 5;
        int newLine = 5;
        String newStationName = "剑川路";
        House house = room.getHouse();
        house.setBedroom(newBedroom);
        house.setCurrentFloor(newCurrentFloor);
        house.setDetailName(newDetailName);
        house.setLayout(newLayout);
        house.setLivingroom(newLivingroom);
        house.setNotDetailName(newNotDetailName);
        house.setTotalFloor(newTotalFloor);
        house.getLocations().get(0).setDistance(newDistance);
        house.getLocations().get(0).setLine(newLine);
        house.getLocations().get(0).setStationName(newStationName);

        String theOtherRoomId = "2000";
        RoomEntity theOther = roomMap.get(theOtherRoomId);
        List<RoomEntity> roomList = Arrays.asList(new RoomEntity[] { roomEntity, theOther });
        database.moveRoomToHistoryWithHouseChange(roomList);

        roomMap.clear();
        houseMap.clear();
        database.getAllRooms(roomMap, houseMap);
        roomEntity = roomMap.get(roomId);
        room = roomEntity.getRoom();
        Assert.assertEquals(newArea, room.getArea());
        Assert.assertEquals(newNumber, room.getNumber());
        Assert.assertEquals(newOrientation, room.getOrientation());
        Assert.assertEquals(newSeparateBalcony, room.isSeparateBalcony());
        Assert.assertEquals(newSeparateBathroom, room.isSeparateBathroom());
        Assert.assertEquals(newState, room.getState());
        Assert.assertEquals(newRentPerMonth, room.getPrices().get(0).getRentPerMonth());
        Assert.assertEquals(newStyle, room.getStyle().getStyle());
        Assert.assertEquals(newStyleVersion, room.getStyle().getVersion());
        theOther = roomMap.get(theOtherRoomId);
        Assert.assertEquals(theOther.getRoom().getHouse(), roomEntity.getRoom().getHouse());
        house = roomEntity.getRoom().getHouse();
        Assert.assertEquals(newBedroom, house.getBedroom());
        Assert.assertEquals(newCurrentFloor, house.getCurrentFloor());
        Assert.assertEquals(newDetailName, house.getDetailName());
        Assert.assertEquals(newLayout, house.getLayout());
        Assert.assertEquals(newLivingroom, house.getLivingroom());
        Assert.assertEquals(newNotDetailName, house.getNotDetailName());
        Assert.assertEquals(newTotalFloor, house.getTotalFloor());
        Assert.assertEquals(newDistance, house.getLocations().get(0).getDistance());
        Assert.assertEquals(newLine, house.getLocations().get(0).getLine());
        Assert.assertEquals(newStationName, house.getLocations().get(0).getStationName());
    }

    @Test
    public void testMoveWithNoHouseChange()
            throws SQLException, InterruptedException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Database database = new Database(1, url, user, password);
        Util.initDatabase(database);
        Map<String, RoomEntity> roomMap = new HashMap<>();
        Map<String, HouseEntity> houseMap = new HashMap<>();
        database.getAllRooms(roomMap, houseMap);
        RoomEntity roomEntity = roomMap.get(roomId);
        Assert.assertNotNull(roomEntity);
        Timestamp date = new Timestamp(new Date().getTime());
        Room room = roomEntity.getRoom();
        int newArea = 100;
        String newNumber = "new number";
        String newOrientation = "北";
        boolean newSeparateBalcony = false;
        boolean newSeparateBathroom = true;
        State newState = State.Unavailable;
        int newRentPerMonth = 50;
        String newStyle = "米苏";
        int newStyleVersion = 5;
        room.setArea(newArea);
        room.setNumber(newNumber);
        room.setOrientation(newOrientation);
        room.setSeparateBalcony(newSeparateBalcony);
        room.setSeparateBathroom(newSeparateBathroom);
        room.setState(newState);
        room.getPrices().get(0).setRentPerMonth(newRentPerMonth);
        room.getStyle().setStyle(newStyle);
        room.getStyle().setVersion(newStyleVersion);
        roomEntity.setBegin(date);
        roomEntity.setEnd(date);
        database.moveRoomToHistoryWithNoHouseChange(roomEntity);
        roomMap.clear();
        houseMap.clear();
        database.getAllRooms(roomMap, houseMap);
        roomEntity = roomMap.get(roomId);
        room = roomEntity.getRoom();
        Assert.assertEquals(newArea, room.getArea());
        Assert.assertEquals(newNumber, room.getNumber());
        Assert.assertEquals(newOrientation, room.getOrientation());
        Assert.assertEquals(newSeparateBalcony, room.isSeparateBalcony());
        Assert.assertEquals(newSeparateBathroom, room.isSeparateBathroom());
        Assert.assertEquals(newState, room.getState());
        Assert.assertEquals(newRentPerMonth, room.getPrices().get(0).getRentPerMonth());
        Assert.assertEquals(newStyle, room.getStyle().getStyle());
        Assert.assertEquals(newStyleVersion, room.getStyle().getVersion());

    }
}
