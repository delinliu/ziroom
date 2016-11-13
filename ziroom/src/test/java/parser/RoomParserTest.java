package parser;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import util.Util;

public class RoomParserTest {

    final static String availableRoomPath = "src/test/resource/simulator/available-room.html";
    final static String availableRoomWithEmptyDetailNamePath = "src/test/resource/simulator/available-room-with-empty-detail-name.html";
    final static String availableRoomWithEmptyNotDetailNamePath = "src/test/resource/simulator/available-room-with-empty-not-detail-name.html";
    final static String availableRoomWithEmptyRoomNamePath = "src/test/resource/simulator/available-room-with-empty-room-name.html";
    final static String availableRoomWithEmptyRoomNumberPath = "src/test/resource/simulator/available-room-with-empty-room-number.html";
    final static String availableRoomWithNoEllipsisPath = "src/test/resource/simulator/available-room-with-no-ellipsis.html";
    final static String availableRoomWithNoRoomNamePath = "src/test/resource/simulator/available-room-with-no-room-name.html";
    final static String availableRoomWithNoH2Path = "src/test/resource/simulator/available-room-with-not-h2.html";
    final static String availableRoomWithWrongNamePath = "src/test/resource/simulator/available-room-with-wrong-room-name.html";

    @Test
    public void testParseRoom() throws IOException, ParserException {

        String content = Util.readFile(availableRoomPath);
        RoomParser parser = new RoomParser();
        parser.parseRoom(content);
    }

    @Test
    public void testParseRoomDetailException() throws IOException {

        RoomParser parser = new RoomParser();
        String normalContent = Util.readFile(availableRoomPath);

        try {
            parser.parseRoom(normalContent.replaceAll("class=\"detail_room\"", "class=\"detail_room_xxx\""));
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomDetailNotUnique, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("<li><b></b>朝向： 南</li>", ""));
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomDetailLiSizeNot5, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("面积： 11\\.3㎡", "面积：㎡"));
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomDetailArea, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("朝向： 南", "朝向： 不知道"));
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomDetailOrientation, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("户型： 3室1厅", "户型： ?室?厅"));
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomDetailLayout, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("楼层： 04/6层", "楼层： ?/6层"));
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomDetailFloor, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("交通：", "??："));
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomDetailLocation, e.getMessage());
        }

        try {
            parser.parseRoom(normalContent.replaceAll("距6号线云山路435米", "距?号线云山路435米"));
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomDetailLocationDetail, e.getMessage());
        }

    }

    @Test
    public void testParseRoomNameException() throws IOException {

        RoomParser parser = new RoomParser();

        try {
            String content = Util.readFile(availableRoomWithEmptyDetailNamePath);
            parser.parseRoom(content);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomNameDetailOrNumberEmpty, e.getMessage());
        }

        try {
            String content = Util.readFile(availableRoomWithEmptyNotDetailNamePath);
            parser.parseRoom(content);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomNameNotDetailEmpty, e.getMessage());
        }

        try {
            String content = Util.readFile(availableRoomWithEmptyRoomNamePath);
            parser.parseRoom(content);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomNameBlank, e.getMessage());
        }

        try {
            String content = Util.readFile(availableRoomWithEmptyRoomNumberPath);
            parser.parseRoom(content);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomNameDetailOrNumberEmpty, e.getMessage());
        }

        try {
            String content = Util.readFile(availableRoomWithNoEllipsisPath);
            parser.parseRoom(content);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomNameEllipsisEmpty, e.getMessage());
        }

        try {
            String content = Util.readFile(availableRoomWithNoRoomNamePath);
            parser.parseRoom(content);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomNameNotUnique, e.getMessage());
        }

        try {
            String content = Util.readFile(availableRoomWithNoH2Path);
            parser.parseRoom(content);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errH2NotUniqueOfRoomName, e.getMessage());
        }

        try {
            String content = Util.readFile(availableRoomWithWrongNamePath);
            parser.parseRoom(content);
        } catch (ParserException e) {
            Assert.assertEquals(RoomParser.errRoomNameFormat, e.getMessage());
        }

    }
}
