package parser;

import entity.Room;

public interface Parser {

    public Room parseRoom(String content) throws ParserException;
}
