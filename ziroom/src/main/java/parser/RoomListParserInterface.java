package parser;

import java.util.Set;

public interface RoomListParserInterface {

    /**
     *  Parse the html content, and return a room id list.
     *  
     *  PS: html content is the detail page of ziroom.
     *      e.g. http://sh.ziroom.com/z/nl/z2-s6%E5%8F%B7%E7%BA%BF.html?p=1
     */
    public Set<String> parseRoomList(String content) throws ParserException;
}
