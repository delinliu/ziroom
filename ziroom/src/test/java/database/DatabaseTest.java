package database;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import entity.House;
import entity.Room;
import entity.State;

public class DatabaseTest {

    private String roomId = "1000";

    private void initDatabase(Database database) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
        Method method = Database.class.getDeclaredMethod("createOneConnection", new Class<?>[] {});
        method.setAccessible(true);
        Connection connection = (Connection) method.invoke(database);
        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        statement.execute("delete from history_location where id>0;");
        statement.execute("delete from history_price where id>0;");
        statement.execute("delete from history_room where id>0;");
        statement.execute("delete from history_house where id>0;");
        statement.execute("delete from location where id>0;");
        statement.execute("delete from price where id>0;");
        statement.execute("delete from room where id>0;");
        statement.execute("delete from house where id>0;");
        statement.execute(
                "INSERT INTO `house` VALUES ('1', '1', 'detail name', 'not detail name', 'layout', '3', '1', '5', '10');");
        statement.execute("INSERT INTO `location` VALUES ('10', '1', '6', 'station name', '100');");
        statement.execute("INSERT INTO `location` VALUES ('20', '1', '6', 'station name 2', '200');");
        statement.execute("INSERT INTO `location` VALUES ('30', '1', '6', 'station name 3', '300');");
        statement.execute("INSERT INTO `price` VALUES ('100', '1000', '2000', '2000', '2100', '月付');");
        statement.execute("INSERT INTO `price` VALUES ('200', '1000', '1900', '1900', '2000', '季付');");
        statement.execute("INSERT INTO `price` VALUES ('300', '1000', '1900', '1900', '1900', '半年付');");
        statement.execute("INSERT INTO `price` VALUES ('400', '1000', '1900', '1900', '1800', '年付');");
        statement.execute("INSERT INTO `price` VALUES ('500', '2000', '3000', '3000', '3100', '月付');");
        statement.execute("INSERT INTO `price` VALUES ('600', '2000', '2900', '2900', '3000', '季付');");
        statement.execute("INSERT INTO `price` VALUES ('700', '2000', '2900', '2900', '2900', '半年付');");
        statement.execute("INSERT INTO `price` VALUES ('800', '2000', '2900', '2900', '2800', '年付');");
        statement.execute(
                "INSERT INTO `room` VALUES ('10000', '1', '1000', 'number', '10', '南', '木棉', '4', '1', '0', 'Available', '2016-11-19 14:08:09', '2016-11-21 15:28:08');");
        statement.execute(
                "INSERT INTO `room` VALUES ('20000', '1', '2000', 'number', '15', '南', '拿铁', '4', '0', '1', 'Unavailable', '2016-11-16 14:08:09', '2016-11-21 14:08:13');");
        connection.commit();
        connection.setAutoCommit(true);
        connection.close();
    }

    private String url = "jdbc:mysql://127.0.0.1/ziroom_test";
    private String user = "root";
    private String password = "123456";

    @Test
    public void testGetRoom() throws ClassNotFoundException, InterruptedException, SQLException, NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Database database = new Database(1, url, user, password);
        initDatabase(database);
        Map<String, RoomEntity> roomMap = database.getAllRooms();
        Assert.assertEquals(2, roomMap.size());
        RoomEntity roomEntity = roomMap.get(roomId);
        Room room = roomEntity.getRoom();
        Assert.assertNotNull(roomEntity);
        Assert.assertEquals(10000, roomEntity.getRoomIdLocal());
        Assert.assertEquals("1", room.getHouse().getHouseId());
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
        House house = room.getHouse();
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
    public void testUpdateEndTime()
            throws ClassNotFoundException, InterruptedException, SQLException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Database database = new Database(1, url, user, password);
        initDatabase(database);
        Map<String, RoomEntity> roomMap = database.getAllRooms();
        RoomEntity roomEntity = roomMap.get(roomId);
        Timestamp date = new Timestamp(new Date().getTime());
        roomEntity.setNewEnd(date);
        database.updateRoomEndTime(roomEntity);
        roomMap = database.getAllRooms();
        Assert.assertTrue(Math.abs(date.getTime() - roomMap.get(roomId).getEnd().getTime()) <= 1000);
    }

    @Test
    public void testMoveWithHouseChange()
            throws SQLException, InterruptedException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Database database = new Database(1, url, user, password);
        initDatabase(database);
        Map<String, RoomEntity> roomMap = database.getAllRooms();
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
        int newDistance = 10000;
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

        roomMap = database.getAllRooms();
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
        initDatabase(database);
        Map<String, RoomEntity> roomMap = database.getAllRooms();
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
        roomMap = database.getAllRooms();
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
