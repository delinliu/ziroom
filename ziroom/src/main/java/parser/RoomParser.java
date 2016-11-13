package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import entity.House;
import entity.Location;
import entity.Room;

public class RoomParser implements Parser {

    final public static String errRoomNameNotUnique = "Tag of class [room_name] is not unique.";
    final public static String errH2NotUniqueOfRoomName = "Tag [h2] under tag of class [room_name] is not unique.";
    final public static String errRoomNameBlank = "Text of [room_name:h2] is blank";
    final public static String errRoomNameFormat = "Room name format error.";
    final public static String errRoomNameDetailOrNumberEmpty = "House detail name or room number is empty.";
    final public static String errRoomNameEllipsisEmpty = "Tag of class [ellipsis] is not unique.";
    final public static String errRoomNameNotDetailEmpty = "House not detail name is empty.";

    final public static String errRoomDetailNotUnique = "Class [detail_room] is not unique.";
    final public static String errRoomDetailLiSizeNot5 = "Amount of tag [li] under [detail_room] is not 5.";
    final public static String errRoomDetailArea = "Room area format error.";
    final public static String errRoomDetailOrientation = "Room orientation format error.";
    final public static String errRoomDetailLayout = "Room layout format error.";
    final public static String errRoomDetailFloor = "Room floor format error.";
    final public static String errRoomDetailLocation = "Room location format error.";
    final public static String errRoomDetailLocationDetail = "Room location detail format error.";

    private boolean isUnique(Elements eles) {
        return eles != null && eles.size() == 1;
    }

    private boolean isSizeEqual(Elements eles, int size) {
        return eles != null && size == eles.size();
    }

    @Override
    public Room parseRoom(String content) throws ParserException {

        Document document = Jsoup.parse(content);
        House house = new House();
        Room room = new Room();
        room.setHouse(house);

        parseRoomNames(house, room, document);

        parseRoomDetail(house, room, document);

        // TODO: Other parse actions

        return room;
    }

    private void parseRoomDetail(House house, Room room, Document document) throws ParserException {

        Elements detail = document.getElementsByClass("detail_room");
        if (!isUnique(detail)) {
            throw new ParserException(errRoomDetailNotUnique);
        }

        Elements lis = detail.get(0).getElementsByTag("li");
        if (!isSizeEqual(lis, 5)) {
            throw new ParserException(errRoomDetailLiSizeNot5);
        }

        parseRoomDetailArea(house, room, lis.get(0));
        parseRoomDetailOrientation(house, room, lis.get(1));
        parseRoomDetailLayout(house, room, lis.get(2));
        parseRoomDetailFloor(house, room, lis.get(3));
        parseRoomDetailLocation(house, room, lis.get(4));

    }

    private void parseRoomDetailArea(House house, Room room, Element element) throws ParserException {
        String areaText = element.text().trim();
        Matcher matcher = Pattern.compile("面积： *([0-9]+(|\\.[0-9]+))㎡").matcher(areaText);
        if (!matcher.find()) {
            throw new ParserException(errRoomDetailArea);
        }

        int area = (int) (Double.parseDouble(matcher.group(1)) * 100);
        room.setArea(area);
    }

    private void parseRoomDetailOrientation(House house, Room room, Element element) throws ParserException {
        String orientationText = element.text().trim();
        Matcher matcher = Pattern.compile("朝向： *([东南西北]+)").matcher(orientationText);
        if (!matcher.find()) {
            throw new ParserException(errRoomDetailOrientation);
        }

        String orientation = matcher.group(1);
        room.setOrientation(orientation);
    }

    private void parseRoomDetailLayout(House house, Room room, Element element) throws ParserException {
        String orientationText = element.ownText().trim();
        Matcher matcher = Pattern.compile("户型： *(([0-9]+)室([0-9]+)厅)").matcher(orientationText);
        if (!matcher.find()) {
            throw new ParserException(errRoomDetailLayout);
        }

        String layout = matcher.group(1);
        int livingroom = Integer.parseInt(matcher.group(2));
        int bedroom = Integer.parseInt(matcher.group(3));
        house.setLayout(layout);
        house.setLivingroom(livingroom);
        house.setBedroom(bedroom);
    }

    private void parseRoomDetailFloor(House house, Room room, Element element) throws ParserException {
        String floorText = element.text().trim();
        Matcher matcher = Pattern.compile("楼层： *([0-9]+)/([0-9]+)层").matcher(floorText);
        if (!matcher.find()) {
            throw new ParserException(errRoomDetailFloor);
        }

        int currentFloor = Integer.parseInt(matcher.group(1));
        int totalFloor = Integer.parseInt(matcher.group(2));
        house.setCurrentFloor(currentFloor);
        house.setTotalFloor(totalFloor);
    }

    private void parseRoomDetailLocation(House house, Room room, Element element) throws ParserException {
        String locationHeadText = element.ownText().trim();
        if (!"交通：".equals(locationHeadText)) {
            throw new ParserException(errRoomDetailLocation);
        }

        List<String> lineTexts = new ArrayList<>();
        lineTexts.add(element.getElementById("lineList").ownText());
        for (Element p : element.getElementsByTag("p")) {
            lineTexts.add(p.text());
        }

        List<Location> locations = new ArrayList<>();
        Pattern pattern = Pattern.compile("距([0-9]+)号线([^0-9]+)([0-9]+)米");
        for (String locationText : lineTexts) {
            locationText = locationText.trim();
            Matcher matcher = pattern.matcher(locationText);
            if (!matcher.find()) {
                throw new ParserException(errRoomDetailLocationDetail);
            }
            int line = Integer.parseInt(matcher.group(1));
            String stationName = matcher.group(2);
            int distance = Integer.parseInt(matcher.group(3));

            Location location = new Location();
            location.setLine(line);
            location.setStationName(stationName);
            location.setDistance(distance);
        }
        house.setLocations(locations);
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
