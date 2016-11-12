package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
    public void testParseRoomException() throws IOException {

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
