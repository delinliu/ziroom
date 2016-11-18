package parser;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import util.Util;

public class RoomListParserTest {

    final static String roomListPath = "src/test/resource/simulator/room-list.html";
    final static String roomListErrorPath = "src/test/resource/simulator/room-list-error.html";

    @Test
    public void testParse() throws IOException, ParserException {

        RoomListParserInterface parser = new RoomListParser();
        String normalContent = Util.readFile(roomListPath);

        Set<String> roomList = parser.parseRoomList(normalContent);
        Assert.assertEquals(18, roomList.size());

        Set<String> targetSet = new HashSet<String>(Arrays.asList(new String[] { "60275552", "60310524", "60310527",
                "60293346", "60299676", "60292861", "60306508", "60310458", "60311337", "60301226", "60311334",
                "60293347", "60302039", "60300612", "60312569", "60306445", "60300572", "60275020" }));
        Assert.assertEquals(targetSet, roomList);
    }

    @Test
    public void testEnd() throws IOException {

        RoomListParserInterface parser = new RoomListParser();
        String errorContent = Util.readFile(roomListErrorPath);
        try {
            parser.parseRoomList(errorContent);
        } catch (ParserException e) {
            Assert.assertEquals(RoomListParser.errRoomListNoMore, e.getMessage());
        }
    }

    @Test
    public void testException() throws IOException {

        RoomListParserInterface parser = new RoomListParser();
        String normalContent;

        try {
            normalContent = Util.readFile(roomListPath);
            parser.parseRoomList(normalContent.replaceAll("id=\"houseList\"", "id=\"houseList_xxx\""));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomListParser.errRoomListNoHouseList, e.getMessage());
        }

        try {
            normalContent = Util.readFile(roomListPath);
            parser.parseRoomList(normalContent.replaceAll("<ul id=\"houseList\">",
                    "<ul id=\"houseList\"></ul><ul id=\"houseList_xxx\">"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomListParser.errRoomListNoHouseListLi, e.getMessage());
        }

        try {
            normalContent = Util.readFile(roomListPath);
            parser.parseRoomList(normalContent.replaceFirst("//sh\\.ziroom\\.com/z/vr/60275552\\.html",
                    "//sh\\.ziroom\\.com/z/vr/60275553\\.html"));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomListParser.errRoomListLiAFormat, e.getMessage());
        }

        try {
            normalContent = Util.readFile(roomListPath);
            parser.parseRoomList(normalContent.replaceFirst("//sh\\.ziroom\\.com/z/vr/60275552\\.html", ""));
            Assert.assertTrue(false);
        } catch (ParserException e) {
            Assert.assertEquals(RoomListParser.errRoomListLiASizeNot4, e.getMessage());
        }
    }
}
