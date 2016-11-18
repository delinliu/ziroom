package parser;

import entity.Room;

public interface RoomParserInterface {

    /**
     *  Parse the html content, and fill data in Room.
     *  If any field is wrong or empty, throw ParserException.
     *  
     *  PS: html content is the detail page of ziroom.
     *      e.g. http://sh.ziroom.com/z/vr/60275552.html
     */
    public Room parseRoom(String content) throws ParserException;
}
