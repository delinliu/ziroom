package parser;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import entity.House;
import entity.Room;

public class RoomParser implements Parser {

    final public static String errRoomNameNotUnique = "Tag of class [room_name] is not unique.";
    final public static String errH2NotUniqueOfRoomName = "Tag [h2] under tag of class [room_name] is not unique.";
    final public static String errRoomNameBlank = "Text of [room_name:h2] is blank";
    final public static String errRoomNameFormat = "Room name format error.";
    final public static String errRoomNameDetailOrNumberEmpty = "House detail name or room number is empty.";
    final public static String errRoomNameEllipsisEmpty = "Tag of class [ellipsis] is not unique.";
    final public static String errRoomNameNotDetailEmpty = "House not detail name is empty.";

    private boolean isUnique(Elements eles) {
        return eles != null && eles.size() == 1;
    }

    @Override
    public Room parseRoom(String content) throws ParserException {

        Document document = Jsoup.parse(content);
        House house = new House();
        Room room = new Room();
        room.setHouse(house);

        parseRoomNames(house, room, document);

        // TODO: Other parse actions

        return room;
    }

    /**
     *  Try to parse 3 fields: [house-detail-name], [room-number], [house-not-detail-name].
     *  Every filed of them must be not blank. Fill them into House and Room.
     *   
     *  <xxx class="room_name">
     *      <h2>
     *          [house-detail-name]-[room-number]
     *      </h2>
     *      <xxx class="ellipsis">
     *          [house-not-detail-name]
     *      </xxx>
     *  </xxx>
     */
    private void parseRoomNames(House house, Room room, Document document) throws ParserException {

        Elements roomNames = document.getElementsByClass("room_name");
        if (!isUnique(roomNames)) {
            throw new ParserException(errRoomNameNotUnique);
        }
        Elements h2OfRoomName = roomNames.get(0).getElementsByTag("h2");
        if (!isUnique(h2OfRoomName)) {
            throw new ParserException(errH2NotUniqueOfRoomName);
        }
        String roomName = h2OfRoomName.get(0).text().trim();
        if (StringUtils.isBlank(roomName)) {
            throw new ParserException(errRoomNameBlank);
        }
        int split = roomName.lastIndexOf("-");
        if (split == -1) {
            throw new ParserException(errRoomNameFormat);
        }
        String houseDetailName = roomName.substring(0, split).trim();
        String roomNumber = roomName.substring(split + 1).trim();
        if (StringUtils.isBlank(houseDetailName) || StringUtils.isBlank(roomNumber)) {
            throw new ParserException(errRoomNameDetailOrNumberEmpty);
        }

        Elements ellipsis = roomNames.get(0).getElementsByClass("ellipsis");
        if (!isUnique(ellipsis)) {
            throw new ParserException(errRoomNameEllipsisEmpty);
        }
        String houseNotDetailName = ellipsis.get(0).text().trim().replaceAll(" +", " ");
        if (StringUtils.isBlank(houseNotDetailName)) {
            throw new ParserException(errRoomNameNotDetailEmpty);
        }

        house.setDetailName(houseDetailName);
        house.setNotDetailName(houseNotDetailName);
        room.setNumber(roomNumber);
    }

}
