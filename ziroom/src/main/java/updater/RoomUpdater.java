package updater;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import database.Database;
import database.HouseEntity;
import database.RoomEntity;
import entity.House;
import entity.Room;

public class RoomUpdater {

    private BlockingQueue<Room> queue;
    private Database database;
    private Map<String, RoomEntity> roomMap;
    private Map<String, HouseEntity> houseMap;
    private boolean isRunning = false;
    private ExecutorService executor;

    public RoomUpdater(BlockingQueue<Room> queue, Database database, Map<String, RoomEntity> roomMap,
            Map<String, HouseEntity> houseMap) {
        this.queue = queue;
        this.database = database;
        this.roomMap = roomMap;
        this.houseMap = houseMap;
    }

    private Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    public void update() throws InterruptedException, SQLException {
        Room room = queue.take();
        House house = room.getHouse();
        String roomId = room.getRoomId();
        String houseId = house.getHouseId();
        RoomEntity roomEntity = roomMap.get(roomId);
        HouseEntity houseEntity = houseMap.get(houseId);

        Timestamp now = now();
        if (houseEntity == null && roomEntity == null) {
            System.out.println(String.format("Updater, new house[%s] and new room[%s].", houseId, roomId));
            houseEntity = new HouseEntity();
            houseEntity.setHouse(house);
            houseEntity.setHouseIdLocal(-1);
            roomEntity = new RoomEntity();
            roomEntity.setRoom(room);
            roomEntity.setBegin(now);
            roomEntity.setEnd(now);
            roomEntity.setNewEnd(now);
            roomEntity.setHouseEntity(houseEntity);
            roomEntity.setRoomIdLocal(-1);
            database.addHouseAndRoom(roomEntity);
            houseEntity.setHouseIdLocal(roomEntity.getHouseIdLocal());
            roomMap.put(roomId, roomEntity);
            houseMap.put(houseId, houseEntity);
        } else if (houseEntity != null && roomEntity == null) {
            System.out.println(String.format("Updater, old house[%s] and new room[%s].", houseId, roomId));

            // Insert a new room into database, a old house is connected with it.
            roomEntity = new RoomEntity();
            roomEntity.setRoom(room);
            roomEntity.setBegin(now);
            roomEntity.setEnd(now);
            roomEntity.setNewEnd(now);
            roomEntity.setRoomIdLocal(-1);
            database.addRoom(roomEntity);
            roomEntity.setHouseEntity(houseEntity);
            roomEntity.getRoom().setHouse(houseEntity.getHouse());
            roomMap.put(roomId, roomEntity);

            // House info is changed, so flush the new house into database for all rooms in the house.
            if (!house.equals(houseEntity.getHouse())) {
                System.out.println("Updater, house[%s] is changed.");
                houseEntity.setHouse(house);
                int oldHouseIdLocal = houseEntity.getHouseIdLocal();
                Set<String> roomIds = database.getRoomIds(houseId);
                List<RoomEntity> roomEntities = new ArrayList<>();
                for (String rId : roomIds) {
                    RoomEntity rEntity = roomMap.get(rId);
                    rEntity.setBegin(now);
                    rEntity.setNewEnd(now);
                    rEntity.getRoom().setHouse(house);
                    roomEntities.add(rEntity);
                }
                database.moveRoomToHistoryWithHouseChange(roomEntities);
                int newHouseIdLocal = roomEntity.getHouseIdLocal();
                houseEntity.setHouseIdLocal(newHouseIdLocal);
                System.out.println(oldHouseIdLocal + "->" + newHouseIdLocal);
                System.out.println(oldHouseIdLocal + "->" + houseEntity.getHouseIdLocal());
            }
        } else if (houseEntity != null && roomEntity != null) {
            System.out.println(String.format("Updater, old house[%s] and old room[%s].", houseId, roomId));
            if (!house.equals(houseEntity.getHouse())) { // House changed
                System.out.println(String.format("Updater, house[%s] is changed.", houseId));
                houseEntity.setHouse(house);
                Set<String> roomIds = database.getRoomIds(houseId);
                List<RoomEntity> roomEntities = new ArrayList<>();
                for (String rId : roomIds) {
                    RoomEntity rEntity = roomMap.get(rId);
                    if (rId.equals(roomId)) {
                        rEntity.setRoom(room);
                    }
                    rEntity.setBegin(now);
                    rEntity.setNewEnd(now);
                    rEntity.getRoom().setHouse(house);
                    roomEntities.add(rEntity);
                }
                database.moveRoomToHistoryWithHouseChange(roomEntities);
            } else if (!room.equals(roomEntity.getRoom())) { // House not changed, Room changed
                System.out.println(String.format("Updater, room[%s] is changed.", roomId));
                room.setHouse(houseEntity.getHouse());
                roomEntity.setRoom(room);
                roomEntity.setBegin(now);
                roomEntity.setNewEnd(now);
                database.moveRoomToHistoryWithNoHouseChange(roomEntity);
            } else { // House not changed, Room not changed
                System.out.println(String.format("Updater, just update time for room[%s].", roomId));
                roomEntity.setEnd(now);
                roomEntity.setNewEnd(now);
                database.updateRoomEndTime(roomEntity);
            }
        } else { // houseEntity == null && roomEntity != null, it's impossible
            System.err.println("Error: [houseEntity == null && roomEntity != null] roomId");
        }
    }

    public void start() {
        if (isRunning) {
            return;
        }
        executor = Executors.newFixedThreadPool(1);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        update();
                    } catch (InterruptedException e) {
                        // e.printStackTrace();
                        break;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Room updater quit.");
            }
        });
        isRunning = true;
        System.out.println("Room updater started.");
    }

    public void stop() {
        if (!isRunning) {
            return;
        }
        executor.shutdownNow();
        isRunning = false;
        System.out.println("Room updater stoped.");
    }
}
