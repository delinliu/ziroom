package parser;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import entity.House;
import entity.Location;
import entity.Price;
import entity.Room;
import entity.State;
import entity.Style;
import util.Util;

public class RoomParserTest {

    final static String availableRoomPath = "src/test/resource/simulator/available-room.html";

    @Test
    public void testParseRoom() throws IOException, ParserException {

        String content = Util.readFile(availableRoomPath);
        RoomParser parser = new RoomParser();

        Room room = parser.parseRoom(content);
        Assert.assertEquals(1130, room.getArea());
        Assert.assertEquals("02卧", room.getNumber());
        Assert.assertEquals("南", room.getOrientation());
        Assert.assertEquals("60300024", room.getRoomId());
        Assert.assertEquals(State.Available, room.getState());
        Assert.assertFalse(room.isSeparateBalcony());
        Assert.assertFalse(room.isSeparateBathroom());

        List<Price> prices = room.getPrices();
        Assert.assertNotNull(prices);
        Assert.assertEquals(4, prices.size());
        Price firstPrice = prices.get(0);
        Assert.assertEquals(2160, firstPrice.getDeposit());
        Assert.assertEquals("月付", firstPrice.getDesc());
        Assert.assertEquals(2160, firstPrice.getRentPerMonth());
        Assert.assertEquals(2592, firstPrice.getServicePerYear());

        Style style = room.getStyle();
        Assert.assertNotNull(style);
        Assert.assertEquals("布丁", style.getStyle());
        Assert.assertEquals(40, style.getVersion());

        House house = room.getHouse();
        Assert.assertNotNull(house);
        Assert.assertEquals("黄山新村3居室", house.getDetailName());
        Assert.assertEquals("60049455", house.getHouseId());
        Assert.assertEquals("3室1厅", house.getLayout());
        Assert.assertEquals("[浦东 金杨] 6号线 云山路", house.getNotDetailName());
        Assert.assertEquals(3, house.getBedroom());
        Assert.assertEquals(4, house.getCurrentFloor());
        Assert.assertEquals(1, house.getLivingroom());
        Assert.assertEquals(6, house.getTotalFloor());

        List<Location> locations = house.getLocations();
        Assert.assertNotNull(locations);
        Assert.assertEquals(3, locations.size());
        Location firstLocation = locations.get(0);
        Assert.assertEquals(435, firstLocation.getDistance());
        Assert.assertEquals(6, firstLocation.getLine());
        Assert.assertEquals("云山路", firstLocation.getStationName());

        room = parser.parseRoom(content.replaceAll("<p class=\"room_tags clearfix\">",
                "<p class=\"room_tags clearfix\"><span class=\"balcony\"></span><span class=\"toilet\"></span>"));
        Assert.assertTrue(room.isSeparateBalcony());
        Assert.assertTrue(room.isSeparateBathroom());

        room = parser.parseRoom(content.replaceAll("我要看房", "已出租"));
        Assert.assertEquals(State.Unavailable, room.getState());
    }

    @Test
    public void testParseRoomAndHouseId() throws IOException {

        RoomParser parser = new RoomParser();
        String normalContent = Util.readFile(availableRoomPath);

        try {
            parser.parseRoom(normalContent.replaceAll("id=\"room_id\"", ""));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomOrHouseId, e.getMessage());
        }
    }

    @Test
    public void testParseRoomStateException() throws IOException {

        RoomParser parser = new RoomParser();
        String normalContent = Util.readFile(availableRoomPath);

        try {
            parser.parseRoom(normalContent.replaceAll("class=\"room_btns", "class=\"room_btns_xxx"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomStateRoomBtnsNotUnique, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("<div class=\"room_btns clearfix\">",
                    "<div class=\"room_btns clearfix\"></div><div>"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomStateASize0, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("我要看房", "???"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomStateFormat, e.getMessage());
        }
    }

    @Test
    public void testParseRoomTagsException() throws IOException {

        RoomParser parser = new RoomParser();
        String normalContent = Util.readFile(availableRoomPath);

        try {
            parser.parseRoom(normalContent.replaceAll("class=\"room_tags", "class=\"room_tags_xxx"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomTagsNotUqniue, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("<p class=\"room_tags clearfix\">",
                    "<p class=\"room_tags clearfix\"><span></span>"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomTagsUnknownClass, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("<p class=\"room_tags clearfix\">",
                    "<p class=\"room_tags clearfix\"><span class=\"style\"></span>"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomTagsStyleNotUnique, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("<span class=\"style\">风格4\\.0 布丁</span>",
                    "<span class=\"style\">风格?.0 布丁</span>"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomTagsStyleFormat, e.getMessage());
        }
    }

    @Test
    public void testParseRoomPricesException() throws IOException {

        RoomParser parser = new RoomParser();
        String normalContent = Util.readFile(availableRoomPath);

        try {
            parser.parseRoom(normalContent.replaceAll("class=\"payCon\"", "class=\"payCon_xxx\""));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomPricesPayConNotUnique, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("<table>", "<table><tr></tr>"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomPricesTrSizeNot5, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("服务费", ""));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomPriceHeader, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("<td>￥2160/月</td>", ""));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomPrice, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("半年付", "unknown"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomPriceDesc, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("<td>￥2160/月</td>", "<td>￥???/月</td>"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomPriceRent, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("<td>￥2160</td>", "<td>￥???</td>"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomPriceDeposit, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("2592元/年", "???元/年"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomPriceService, e.getMessage());
        }
    }

    @Test
    public void testParseRoomDetailException() throws IOException {

        RoomParser parser = new RoomParser();
        String normalContent = Util.readFile(availableRoomPath);

        try {
            parser.parseRoom(normalContent.replaceAll("class=\"detail_room\"", "class=\"detail_room_xxx\""));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomDetailNotUnique, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("<li><b></b>朝向： 南</li>", ""));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomDetailLiSizeNot5, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("面积： 11\\.3㎡", "面积：㎡"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomDetailArea, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("朝向： 南", "朝向： 不知道"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomDetailOrientation, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("户型： 3室1厅", "户型： ?室?厅"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomDetailLayout, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("楼层： 04/6层", "楼层： ?/6层"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomDetailFloor, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("交通：", "??："));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomDetailLocation, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("距6号线云山路435米", "距?号线云山路435米"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomDetailLocationDetail, e.getMessage());
        }

    }

    @Test
    public void testParseRoomNameException() throws IOException {

        RoomParser parser = new RoomParser();
        String normalContent = Util.readFile(availableRoomPath);

        try {
            parser.parseRoom(normalContent.replaceAll("<h2>黄山新村3居室-02卧</h2>", "<h2>-02卧</h2>"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomNameDetailOrNumberEmpty, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("\\[浦东 金杨\\] 6号线 云山路", ""));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomNameNotDetailEmpty, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("<h2>黄山新村3居室-02卧</h2>", "<h2></h2>"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomNameBlank, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("<h2>黄山新村3居室-02卧</h2>", "<h2>黄山新村3居室-</h2>"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomNameDetailOrNumberEmpty, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("class=\"ellipsis\"", ""));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomNameEllipsisEmpty, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("class=\"room_name\"", ""));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomNameNotUnique, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("<h2>黄山新村3居室-02卧</h2>", "<h3>黄山新村3居室-02卧</h3>"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errH2NotUniqueOfRoomName, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("<h2>黄山新村3居室-02卧</h2>", "<h2>黄山新村3居室 02卧</h2>"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomNameFormat, e.getMessage());
        }

    }
}
