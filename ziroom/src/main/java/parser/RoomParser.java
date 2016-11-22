package parser;

import java.util.ArrayList;
import java.util.Collections;
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
import entity.Price;
import entity.Room;
import entity.State;
import entity.Style;

public class RoomParser implements RoomParserInterface {

    final public static String errRoomNotFound = "No room found.";

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

    final public static String errRoomPricesPayConNotUnique = "Class [payCon] is not unique.";
    final public static String errRoomPricesTrSizeNot5 = "Amount of tag [tr] under [payCon] is not 5.";
    final public static String errRoomPriceHeader = "Room price header format error.";
    final public static String errRoomPrice = "Room price format error.";
    final public static String errRoomPriceDesc = "Room price desc format error.";
    final public static String errRoomPriceRent = "Room price rent format error.";
    final public static String errRoomPriceDeposit = "Room price deposit format error.";
    final public static String errRoomPriceService = "Room price service format error.";

    final public static String errRoomTagsNotUqniue = "Class [room_tags] is not unique.";
    final public static String errRoomTagsUnknownClass = "Unknown class in [room_tags] span.";
    final public static String errRoomTagsStyleNotUnique = "Class [room_tags:style] is not unique.";
    final public static String errRoomTagsStyleFormat = "Style format error.";

    final public static String errRoomStateRoomBtnsNotUnique = "Class [room_btns] is not unique.";
    final public static String errRoomStateASize0 = "Amount of tag [a] under [room_btns] is not 0.";
    final public static String errRoomStateFormat = "Room state format error.";

    final public static String errRoomOrHouseId = "Room id or house id is empty.";

    private boolean isUnique(Elements eles) {
        return eles != null && eles.size() == 1;
    }

    private boolean isSizeEqual(Elements eles, int size) {
        return eles != null && size == eles.size();
    }

    @Override
    public Room parseRoom(String content) throws ParserException {

        if (isNoRoom(content)) {
            throw new ParserException(errRoomNotFound);
        }

        Document document = Jsoup.parse(content);
        House house = new House();
        Room room = new Room();
        room.setHouse(house);

        parseRoomAndHouseId(house, room, document);
        parseRoomNames(house, room, document);
        parseRoomDetail(house, room, document);
        parseRoomPrices(house, room, document);
        parseRoomTags(house, room, document);
        parseRoomState(house, room, document);

        return room;
    }

    private boolean isNoRoom(String content) {
        return "<script>window.location.href='/tips/noRoom.html';</script>".equals(content.trim());
    }

    private void parseRoomAndHouseId(House house, Room room, Document document) throws ParserException {

        Element roomId = document.getElementById("room_id");
        Element houseId = document.getElementById("house_id");
        if (roomId == null || houseId == null) {
            throw new ParserException(errRoomOrHouseId, room.getRoomId());
        }

        house.setHouseId(houseId.attr("value"));
        room.setRoomId(roomId.attr("value"));
    }

    /**
     *  <xxx class="room_btns">
     *      <a>
     *          已出租
     *      </a>
     *      <a>
     *          ...
     *      </a>
     *  </xxx>
     */
    private void parseRoomState(House house, Room room, Document document) throws ParserException {

        Elements roomTags = document.getElementsByClass("room_btns");
        if (!isUnique(roomTags)) {
            throw new ParserException(errRoomStateRoomBtnsNotUnique, room.getRoomId());
        }

        Elements as = roomTags.get(0).getElementsByTag("a");
        if (as.isEmpty()) {
            throw new ParserException(errRoomStateASize0, room.getRoomId());
        }

        String stateText = as.get(0).ownText().trim();
        if ("已出租".equals(stateText)) {
            room.setState(State.Unavailable);
        } else if ("我要看房".equals(stateText)) {
            room.setState(State.Available);
        } else if ("已下定".equals(stateText)) {
            room.setState(State.Unavailable);
        } else {
            throw new ParserException(errRoomStateFormat, room.getRoomId());
        }
    }

    /**
     *  Parse [is_separate_bathroom], [is_separate_balcony], [style.version], [style.style].
     *  
     *  <xxx class="room_tags">
     *      <span class="subway">地铁10分钟</span>
     *      <span class="balcony">独立阳台</span>
     *      <span class="toilet">独卫</span>
     *      <span class="style">风格4.0 布丁</span>
     *  </xxx>
     */
    private void parseRoomTags(House house, Room room, Document document) throws ParserException {

        Elements roomTags = document.getElementsByClass("room_tags");
        if (!isUnique(roomTags)) {
            throw new ParserException(errRoomTagsNotUqniue, room.getRoomId());
        }

        Element roomTag = roomTags.get(0);

        Elements spans = roomTag.getElementsByTag("span");
        for (Element span : spans) {
            if (!span.attr("class").matches("subway|toilet|balcony|style")) {
                throw new ParserException(errRoomTagsUnknownClass, room.getRoomId());
            }
        }

        if (isUnique(roomTag.getElementsByClass("toilet"))) {
            room.setSeparateBathroom(true);
        }
        if (isUnique(roomTag.getElementsByClass("balcony"))) {
            room.setSeparateBalcony(true);
        }

        String style;
        int version;

        Elements styles = roomTag.getElementsByClass("style");
        if (!isUnique(styles)) {
            // throw new ParserException(errRoomTagsStyleNotUnique, room.getRoomId());
            style = "unknown";
            version = 0;
        } else {
            String styleText = styles.get(0).text();
            Matcher matcher = Pattern.compile("风格([0-9]+\\.[0-9]+) *(.*)").matcher(styleText);
            if (!matcher.find()) {
                throw new ParserException(errRoomTagsStyleFormat, room.getRoomId());
            }

            version = (int) (Double.parseDouble(matcher.group(1)) * 10);
            style = matcher.group(2);
        }
        Style roomStyle = new Style();
        roomStyle.setVersion(version);
        roomStyle.setStyle(style);
        room.setStyle(roomStyle);
    }

    /**
     *  Parse [prices] for a room.
     *  Every filed of them must be not blank. Fill them into House and Room.
     *  
     *  <xxx>
     *      <table>
     *          <tr>
     *              <th>方式</th>
     *              <th>租金</th>
     *              <th>押金</th>
     *              <th>服务费</th>
     *          </tr>
     *          <tr>
     *              <td>月付</td>
     *              <td>￥2590/月</td>
     *              <td>￥2590</td>
     *              <td>￥3108元/年</td>
     *          </tr>
     *          <tr>
     *              <td>季付</td>
     *              <td>￥2460/月</td>
     *              <td>￥2460</td>
     *              <td>￥2952元/年</td>
     *          </tr>
     *          <tr>
     *              <td>季付</td>
     *              <td>￥2460/月</td>
     *              <td>￥2460</td>
     *              <td>￥2509元/年</td>
     *          </tr>
     *          <tr>
     *              <td>季付</td>
     *              <td>￥2460/月</td>
     *              <td>￥2460</td>
     *              <td>￥2066元/年</td>
     *          </tr>
     *      </table>
     *  </xxx>
     */
    private void parseRoomPrices(House house, Room room, Document document) throws ParserException {

        Elements payCon = document.getElementsByClass("payCon");
        if (!isUnique(payCon)) {
            throw new ParserException(errRoomPricesPayConNotUnique, room.getRoomId());
        }

        Elements trs = payCon.get(0).getElementsByTag("tr");
        if (!isSizeEqual(trs, 5)) {
            throw new ParserException(errRoomPricesTrSizeNot5, room.getRoomId());
        }

        parseRoomPricesHeader(house, room, trs.get(0));

        room.setPrices(new ArrayList<Price>());
        for (int i = 1; i < trs.size(); i++) {
            parseRoomPrice(house, room, trs.get(i));
        }
        Collections.sort(room.getPrices());
    }

    /**
     *  <tr>
     *      <th>方式</th>
     *      <th>租金</th>
     *      <th>押金</th>
     *      <th>服务费</th>
     *  </tr>
     */
    private void parseRoomPricesHeader(House house, Room room, Element element) throws ParserException {
        Elements ths = element.getElementsByTag("th");
        if (!isSizeEqual(ths, 4) || !"方式".equals(ths.get(0).text()) || !"租金".equals(ths.get(1).text())
                || !"押金".equals(ths.get(2).text()) || !"服务费".equals(ths.get(3).text())) {
            throw new ParserException(errRoomPriceHeader, room.getRoomId());
        }
    }

    /**
     * 
     *  <tr>
     *      <td>月付</td>
     *      <td>￥2590/月</td>
     *      <td>￥2590</td>
     *      <td>￥3108元/年</td>
     *  </tr>
     */
    private void parseRoomPrice(House house, Room room, Element element) throws ParserException {
        Elements tds = element.getElementsByTag("td");
        if (!isSizeEqual(tds, 4)) {
            throw new ParserException(errRoomPrice, room.getRoomId());
        }
        String descText = tds.get(0).text().trim();
        String rentText = tds.get(1).text().trim();
        String depositText = tds.get(2).text().trim();
        String serviceText = tds.get(3).text().trim();

        if (!descText.matches("(月|季|半年|年)付")) {
            throw new ParserException(errRoomPriceDesc, room.getRoomId());
        }

        Matcher matcher = Pattern.compile("￥ *([0-9]+)/月").matcher(rentText);
        if (!matcher.find()) {
            throw new ParserException(errRoomPriceRent, room.getRoomId());
        }
        int rentPerMonth = Integer.parseInt(matcher.group(1));

        matcher = Pattern.compile("￥ *([0-9]+)").matcher(depositText);
        if (!matcher.find()) {
            throw new ParserException(errRoomPriceDeposit, room.getRoomId());
        }
        int deposit = Integer.parseInt(matcher.group(1));

        matcher = Pattern.compile("￥ *([0-9]+)元/年").matcher(serviceText);
        if (!matcher.find()) {
            throw new ParserException(errRoomPriceService, room.getRoomId());
        }
        int servicePerYear = Integer.parseInt(matcher.group(1));

        Price price = new Price();
        price.setDesc(descText);
        price.setRentPerMonth(rentPerMonth);
        price.setDeposit(deposit);
        price.setServicePerYear(servicePerYear);
        room.getPrices().add(price);
    }

    /**
     *  Parse [area], [orientation], [layout], [floor], [location] fields.
     *  Every filed of them must be not blank. Fill them into House and Room.
     *   
     *  <xxx class="detail_room">
     *      <li>
     *          面积： 15.4㎡
     *      </li>
     *      <li>
     *          朝向： 南
     *      </li>
     *      <li>
     *          户型： 3室1厅
     *      </li>
     *      <li>
     *          楼层： 06/6层
     *      </li>
     *      <li>
     *          交通： 
     *          <span id="lineList">
     *              距6号线云山路432米
     *              <span>
     *                  <p>距6号线金桥路1038米</p>
     *                  <p>距6号线德平路1142米</p>
     *              </span
     *          </span>
     *      </li>
     *  </xxx>
     */
    private void parseRoomDetail(House house, Room room, Document document) throws ParserException {

        Elements detail = document.getElementsByClass("detail_room");
        if (!isUnique(detail)) {
            throw new ParserException(errRoomDetailNotUnique, room.getRoomId());
        }

        Elements lis = detail.get(0).getElementsByTag("li");
        if (!isSizeEqual(lis, 5)) {
            throw new ParserException(errRoomDetailLiSizeNot5, room.getRoomId());
        }

        parseRoomDetailArea(house, room, lis.get(0));
        parseRoomDetailOrientation(house, room, lis.get(1));
        parseRoomDetailLayout(house, room, lis.get(2));
        parseRoomDetailFloor(house, room, lis.get(3));
        parseRoomDetailLocation(house, room, lis.get(4));

    }

    /**
     * 面积： 15.4㎡
     */
    private void parseRoomDetailArea(House house, Room room, Element element) throws ParserException {
        String areaText = element.text().trim();
        Matcher matcher = Pattern.compile("面积： *([0-9]+(|\\.[0-9]+))㎡").matcher(areaText);
        if (!matcher.find()) {
            throw new ParserException(errRoomDetailArea, room.getRoomId());
        }

        int area = (int) (Double.parseDouble(matcher.group(1)) * 100);
        room.setArea(area);
    }

    /**
     * 朝向： 南
     */
    private void parseRoomDetailOrientation(House house, Room room, Element element) throws ParserException {
        String orientationText = element.text().trim();
        Matcher matcher = Pattern.compile("朝向： *([东南西北]+)").matcher(orientationText);
        if (!matcher.find()) {
            throw new ParserException(errRoomDetailOrientation, room.getRoomId());
        }

        String orientation = matcher.group(1);
        room.setOrientation(orientation);
    }

    /**
     * 户型： 3室1厅
     */
    private void parseRoomDetailLayout(House house, Room room, Element element) throws ParserException {
        String orientationText = element.ownText().trim();
        Matcher matcher = Pattern.compile("户型： *(([0-9]+)室([0-9]+)厅)").matcher(orientationText);
        if (!matcher.find()) {
            throw new ParserException(errRoomDetailLayout, room.getRoomId());
        }

        String layout = matcher.group(1);
        int livingroom = Integer.parseInt(matcher.group(3));
        int bedroom = Integer.parseInt(matcher.group(2));
        house.setLayout(layout);
        house.setLivingroom(livingroom);
        house.setBedroom(bedroom);
    }

    /**
     * 楼层： 06/6层
     */
    private void parseRoomDetailFloor(House house, Room room, Element element) throws ParserException {
        String floorText = element.text().trim();
        Matcher matcher = Pattern.compile("楼层： *([0-9]+)/([0-9]+)层").matcher(floorText);
        if (!matcher.find()) {
            throw new ParserException(errRoomDetailFloor, room.getRoomId());
        }

        int currentFloor = Integer.parseInt(matcher.group(1));
        int totalFloor = Integer.parseInt(matcher.group(2));
        house.setCurrentFloor(currentFloor);
        house.setTotalFloor(totalFloor);
    }

    /**
     *  <li>
     *      交通： 
     *      <span id="lineList">
     *          距6号线云山路432米
     *          <span>
     *              <p>距6号线金桥路1038米</p>
     *              <p>距6号线德平路1142米</p>
     *          </span
     *      </span>
     *  </li>
     */
    private void parseRoomDetailLocation(House house, Room room, Element element) throws ParserException {
        String locationHeadText = element.ownText().trim();
        if (!"交通：".equals(locationHeadText)) {
            throw new ParserException(errRoomDetailLocation, room.getRoomId());
        }

        List<String> lineTexts = new ArrayList<>();
        lineTexts.add(element.getElementById("lineList").ownText());
        for (Element p : element.getElementsByTag("p")) {
            lineTexts.add(p.text());
        }

        List<Location> locations = new ArrayList<>();
        Pattern pattern = Pattern.compile("距 *([0-9]+) *号线 *([^0-9]+)([0-9]+) *米");
        for (String locationText : lineTexts) {
            locationText = locationText.trim();
            Matcher matcher = pattern.matcher(locationText);
            if (!matcher.find()) {
                throw new ParserException(errRoomDetailLocationDetail, room.getRoomId());
            }
            int line = Integer.parseInt(matcher.group(1));
            String stationName = matcher.group(2);
            int distance = Integer.parseInt(matcher.group(3));

            Location location = new Location();
            location.setLine(line);
            location.setStationName(stationName);
            location.setDistance(distance);
            locations.add(location);
        }
        Collections.sort(locations);
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
            throw new ParserException(errRoomNameNotUnique, room.getRoomId());
        }
        Elements h2OfRoomName = roomNames.get(0).getElementsByTag("h2");
        if (!isUnique(h2OfRoomName)) {
            throw new ParserException(errH2NotUniqueOfRoomName, room.getRoomId());
        }
        String roomName = h2OfRoomName.get(0).text().trim();
        if (StringUtils.isBlank(roomName)) {
            throw new ParserException(errRoomNameBlank, room.getRoomId());
        }
        int split = roomName.lastIndexOf("-");
        if (split == -1) {
            throw new ParserException(errRoomNameFormat, room.getRoomId());
        }
        String houseDetailName = roomName.substring(0, split).trim();
        String roomNumber = roomName.substring(split + 1).trim();
        if (StringUtils.isBlank(houseDetailName) || StringUtils.isBlank(roomNumber)) {
            throw new ParserException(errRoomNameDetailOrNumberEmpty, room.getRoomId());
        }

        Elements ellipsis = roomNames.get(0).getElementsByClass("ellipsis");
        if (!isUnique(ellipsis)) {
            throw new ParserException(errRoomNameEllipsisEmpty, room.getRoomId());
        }
        String houseNotDetailName = ellipsis.get(0).text().trim().replaceAll(" +", " ");
        if (StringUtils.isBlank(houseNotDetailName)) {
            throw new ParserException(errRoomNameNotDetailEmpty, room.getRoomId());
        }

        house.setDetailName(houseDetailName);
        house.setNotDetailName(houseNotDetailName);
        room.setNumber(roomNumber);
    }

}
