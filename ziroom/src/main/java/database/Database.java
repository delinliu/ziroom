package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import entity.House;
import entity.Location;
import entity.Price;
import entity.Room;
import entity.State;
import entity.Style;

public class Database implements DatabaseInterface {

    private String url;
    private String user;
    private String password;
    private BlockingQueue<Connection> connectionQueue;

    public Database(int poolAmount, String url, String user, String password)
            throws ClassNotFoundException, InterruptedException, SQLException {
        this.url = url;
        this.user = user;
        this.password = password;
        initConnectionQueue(poolAmount);
    }

    private void initConnectionQueue(int poolAmount) throws ClassNotFoundException, InterruptedException, SQLException {
        connectionQueue = new ArrayBlockingQueue<Connection>(poolAmount);
        for (int i = 0; i < poolAmount; i++) {
            connectionQueue.put(createOneConnection());
        }
    }

    private Connection createOneConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public void moveRoomToHistoryWithNoHouseChange(RoomEntity roomEntity) throws InterruptedException, SQLException {
        int roomIdLocal = roomEntity.getRoomIdLocal();
        int houseIdLocal = roomEntity.getHouseIdLocal();
        String roomId = roomEntity.getRoom().getRoomId();
        String houseId = roomEntity.getRoom().getHouse().getHouseId();
        Connection connection = connectionQueue.take();
        connection.setAutoCommit(false);
        copyHouseToHistory(houseId, connection);
        copyRoomToHistory(houseIdLocal, roomId, connection);
        copyPriceToHistory(roomIdLocal, roomId, connection);
        copyLocationToHistory(houseIdLocal, houseId, connection);
        deleteLocation(houseId, connection);
        deletePrice(roomId, connection);
        deleteRoom(roomIdLocal, connection);
        deleteHouse(houseIdLocal, connection);
        insertHouse(roomEntity, houseId, connection);
        insertLocations(roomEntity, houseId, connection);
        insertRoom(roomEntity, roomId, houseId, connection);
        insertPrices(roomEntity, roomId, connection);
        connection.commit();
        connection.setAutoCommit(true);
        connectionQueue.put(connection);
    }

    private void insertRoom(RoomEntity roomEntity, String roomId, String houseId, Connection connection)
            throws SQLException {
        Room room = roomEntity.getRoom();
        PreparedStatement statement = connection.prepareStatement(
                "insert into `room`(`houseId`, `roomId`, `number`, `area`, `orientation`, `style`, `styleVersion`, `separateBalcony`, `separateBathroom`, `state`, `begin`, `end`)"
                        + " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, houseId);
        statement.setString(2, roomId);
        statement.setString(3, room.getNumber());
        statement.setInt(4, room.getArea());
        statement.setString(5, room.getOrientation());
        statement.setString(6, room.getStyle().getStyle());
        statement.setInt(7, room.getStyle().getVersion());
        statement.setBoolean(8, room.isSeparateBalcony());
        statement.setBoolean(9, room.isSeparateBathroom());
        statement.setString(10, room.getState().toString());
        statement.setTimestamp(11, new Timestamp(roomEntity.getBegin().getTime()));
        statement.setTimestamp(12, new Timestamp(roomEntity.getEnd().getTime()));
        statement.executeUpdate();
    }

    private void insertPrices(RoomEntity roomEntity, String roomId, Connection connection) throws SQLException {
        List<Price> prices = roomEntity.getRoom().getPrices();
        for (Price price : prices) {
            PreparedStatement statement = connection.prepareStatement(
                    "insert into `price`(`roomId`, `rentPerMonth`, `deposit`, `servicePerYear`, `desc`) values(?, ?, ?, ?, ?)");
            statement.setString(1, roomId);
            statement.setInt(2, price.getRentPerMonth());
            statement.setInt(3, price.getDeposit());
            statement.setInt(4, price.getServicePerYear());
            statement.setString(5, price.getDesc());
            statement.executeUpdate();
        }
    }

    private void insertLocations(RoomEntity roomEntity, String houseId, Connection connection) throws SQLException {
        List<Location> locations = roomEntity.getRoom().getHouse().getLocations();
        for (Location location : locations) {
            PreparedStatement statement = connection.prepareStatement(
                    "insert into `location`(`houseId`, `line`, `stationName`, `distance`) values(?, ?, ?, ?)");
            statement.setString(1, houseId);
            statement.setInt(2, location.getLine());
            statement.setString(3, location.getStationName());
            statement.setInt(4, location.getDistance());
            statement.executeUpdate();
        }
    }

    private void insertHouse(RoomEntity roomEntity, String houseId, Connection connection) throws SQLException {
        House house = roomEntity.getRoom().getHouse();
        PreparedStatement statement = connection.prepareStatement(
                "insert into `house`(`houseId`, `detailName`, `notDetailName`, `layout`, `bedroom`, `livingroom`, `currentFloor`, `totalFloor`) values(?, ?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, houseId);
        statement.setString(2, house.getDetailName());
        statement.setString(3, house.getNotDetailName());
        statement.setString(4, house.getLayout());
        statement.setInt(5, house.getBedroom());
        statement.setInt(6, house.getLivingroom());
        statement.setInt(7, house.getCurrentFloor());
        statement.setInt(8, house.getTotalFloor());
        statement.executeUpdate();
    }

    private void deleteHouse(int houseIdLocal, Connection connection) throws SQLException {
        PreparedStatement statement;
        String deleteHouseSql = "delete from house where id=?";
        statement = connection.prepareStatement(deleteHouseSql);
        statement.setInt(1, houseIdLocal);
        statement.executeUpdate();
    }

    private void deleteRoom(int roomIdLocal, Connection connection) throws SQLException {
        PreparedStatement statement;
        String deleteRoomSql = "delete from room where id=?";
        statement = connection.prepareStatement(deleteRoomSql);
        statement.setInt(1, roomIdLocal);
        statement.executeUpdate();
    }

    private void deletePrice(String roomId, Connection connection) throws SQLException {
        PreparedStatement statement;
        String deletePriceSql = "delete from price where roomId=?";
        statement = connection.prepareStatement(deletePriceSql);
        statement.setString(1, roomId);
        statement.executeUpdate();
    }

    private void deleteLocation(String houseId, Connection connection) throws SQLException {
        PreparedStatement statement;
        String deleteLocationSql = "delete from location where houseId=?";
        statement = connection.prepareStatement(deleteLocationSql);
        statement.setString(1, houseId);
        statement.executeUpdate();
    }

    private void copyLocationToHistory(int houseIdLocal, String houseId, Connection connection) throws SQLException {
        PreparedStatement statement;
        String moveLocationSql = "insert into history_location " + "(select *, ? from location where houseId = ?)";
        statement = connection.prepareStatement(moveLocationSql);
        statement.setInt(1, houseIdLocal);
        statement.setString(2, houseId);
        statement.executeUpdate();
    }

    private void copyPriceToHistory(int roomIdLocal, String roomId, Connection connection) throws SQLException {
        PreparedStatement statement;
        String movePriceSql = "insert into history_price " + "(select *, ? from price where roomId = ?)";
        statement = connection.prepareStatement(movePriceSql);
        statement.setInt(1, roomIdLocal);
        statement.setString(2, roomId);
        statement.executeUpdate();
    }

    private void copyRoomToHistory(int houseIdLocal, String roomId, Connection connection) throws SQLException {
        PreparedStatement statement;
        String moveRoomSql = "insert into history_room " + "(select *, ? from room where roomId = ?)";
        statement = connection.prepareStatement(moveRoomSql);
        statement.setInt(1, houseIdLocal);
        statement.setString(2, roomId);
        statement.executeUpdate();
    }

    private void copyHouseToHistory(String houseId, Connection connection) throws SQLException {
        PreparedStatement statement;
        String moveHouseSql = "insert into history_house" + " (select * from house where houseId = ?)";
        statement = connection.prepareStatement(moveHouseSql);
        statement.setString(1, houseId);
        statement.executeUpdate();
    }

    @Override
    public void updateRoomEndTime(RoomEntity roomEntity) throws SQLException, InterruptedException {
        Connection connection = connectionQueue.take();
        PreparedStatement statement = connection.prepareStatement("update room set end = ? where id = ?");
        statement.setTimestamp(1, new Timestamp(roomEntity.getNewEnd().getTime()));
        statement.setInt(2, roomEntity.getRoomIdLocal());
        statement.executeUpdate();
        statement.close();
        connectionQueue.put(connection);
    }

    @Override
    public Map<String, RoomEntity> getAllRooms() throws SQLException, InterruptedException {
        Connection connection = connectionQueue.take();
        Statement statement = connection.createStatement();
        Map<String, List<Location>> locationMap = loadAllLocations(statement);
        Map<String, List<Price>> priceMap = loadAllPrices(statement);
        Map<String, RoomEntity> roomMap = loadAllRooms(statement, locationMap, priceMap);
        statement.close();
        connectionQueue.put(connection);
        return roomMap;
    }

    private Map<String, RoomEntity> loadAllRooms(Statement statement, Map<String, List<Location>> locationMap,
            Map<String, List<Price>> priceMap) throws SQLException {
        ResultSet resultSet;
        resultSet = statement.executeQuery(
                "select *, room.id as roomIdLocal, house.id as houseIdLocal from room left join house on room.houseId = house.houseId");
        Map<String, RoomEntity> roomMap = new HashMap<>();
        while (resultSet.next()) {
            int houseIdLocal = resultSet.getInt("houseIdLocal");
            String houseId = resultSet.getString("houseId");
            String detailName = resultSet.getString("detailName");
            String notDetailName = resultSet.getString("notDetailName");
            String layout = resultSet.getString("layout");
            int bedroom = resultSet.getInt("bedroom");
            int livingroom = resultSet.getInt("livingroom");
            int currentFloor = resultSet.getInt("currentFloor");
            int totalFloor = resultSet.getInt("totalFloor");
            House house = new House();
            house.setHouseId(houseId);
            house.setDetailName(detailName);
            house.setNotDetailName(notDetailName);
            house.setLayout(layout);
            house.setBedroom(bedroom);
            house.setLivingroom(livingroom);
            house.setCurrentFloor(currentFloor);
            house.setTotalFloor(totalFloor);
            house.setLocations(locationMap.get(houseId));

            int roomIdLocal = resultSet.getInt("roomIdLocal");
            String roomId = resultSet.getString("roomId");
            String number = resultSet.getString("number");
            int area = resultSet.getInt("area");
            String orientation = resultSet.getString("orientation");
            String style = resultSet.getString("style");
            int styleVersion = resultSet.getInt("styleVersion");
            boolean separateBalcony = resultSet.getBoolean("separateBalcony");
            boolean separateBathroom = resultSet.getBoolean("separateBathroom");
            String state = resultSet.getString("state");
            Date begin = resultSet.getTimestamp("begin");
            Date end = resultSet.getTimestamp("end");
            Room room = new Room();
            room.setHouse(house);
            room.setRoomId(roomId);
            room.setNumber(number);
            room.setArea(area);
            room.setOrientation(orientation);
            room.setSeparateBalcony(separateBalcony);
            room.setSeparateBathroom(separateBathroom);
            room.setPrices(priceMap.get(roomId));
            if (State.Available.toString().equals(state)) {
                room.setState(State.Available);
            } else if (State.Unavailable.toString().equals(state)) {
                room.setState(State.Unavailable);
            }
            Style st = new Style();
            st.setStyle(style);
            st.setVersion(styleVersion);
            room.setStyle(st);

            RoomEntity roomEntity = new RoomEntity();
            roomEntity.setBegin(begin);
            roomEntity.setEnd(end);
            roomEntity.setRoomIdLocal(roomIdLocal);
            roomEntity.setHouseIdLocal(houseIdLocal);
            roomEntity.setRoom(room);
            roomMap.put(roomId, roomEntity);
        }
        return roomMap;
    }

    private Map<String, List<Location>> loadAllLocations(Statement statement) throws SQLException {
        ResultSet resultSet;
        resultSet = statement.executeQuery("select * from location");
        Map<String, List<Location>> locationMap = new HashMap<>();
        while (resultSet.next()) {
            Location location = new Location();
            String houseId = resultSet.getString("houseId");
            int line = resultSet.getInt("line");
            String stationName = resultSet.getString("stationName");
            int distance = resultSet.getInt("distance");
            location.setDistance(distance);
            location.setLine(line);
            location.setStationName(stationName);
            if (!locationMap.containsKey(houseId)) {
                locationMap.put(houseId, new ArrayList<>());
            }
            List<Location> locations = locationMap.get(houseId);
            locations.add(location);
        }
        return locationMap;
    }

    private Map<String, List<Price>> loadAllPrices(Statement statement) throws SQLException {
        ResultSet resultSet;
        resultSet = statement.executeQuery("select * from price");
        Map<String, List<Price>> priceMap = new HashMap<>();
        while (resultSet.next()) {
            Price price = new Price();
            String roomId = resultSet.getString("roomId");
            int rentPerMonth = resultSet.getInt("rentPerMonth");
            int deposit = resultSet.getInt("deposit");
            int servicePerYear = resultSet.getInt("servicePerYear");
            String desc = resultSet.getString("desc");
            price.setDeposit(deposit);
            price.setDesc(desc);
            price.setRentPerMonth(rentPerMonth);
            price.setServicePerYear(servicePerYear);
            if (!priceMap.containsKey(roomId)) {
                priceMap.put(roomId, new ArrayList<>());
            }
            List<Price> prices = priceMap.get(roomId);
            prices.add(price);
        }
        return priceMap;
    }

}
