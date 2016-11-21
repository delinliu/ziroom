package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    public Map<String, RoomEntity> getAllRooms() throws InterruptedException, SQLException {
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
            if (!locationMap.containsKey("houseId")) {
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
