package parser;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RoomListParser implements RoomListParserInterface {

    final static public String errRoomListNoHouseList = "Cannot find id [houseList]";
    final static public String errRoomListNoHouseListLi = "Cannot find [houseList:li]";
    final static public String errRoomListLiAFormat = "Format error, [houseList:li:a]";
    final static public String errRoomListLiASizeNot4 = "Format error, [houseList:li:a] does NOT contain 4 a tag.";

    @Override
    public Set<String> parseRoomList(String content) throws ParserException {

        Document document = Jsoup.parse(content);
        Element houseList = document.getElementById("houseList");
        if (houseList == null) {
            throw new ParserException(errRoomListNoHouseList);
        }

        Elements lis = houseList.getElementsByTag("li");
        if (lis.isEmpty()) {
            throw new ParserException(errRoomListNoHouseListLi);
        }

        Set<String> ids = new HashSet<>();
        Pattern pattern = Pattern.compile("sh\\.ziroom\\.com/z/vr/([0-9]+)\\.html");
        for (Element li : lis) {
            Elements as = li.getElementsByTag("a");
            String id = null;
            int amount = 0;
            for (Element a : as) {
                String href = a.attr("href");
                Matcher matcher = pattern.matcher(href);
                if (matcher.find()) {
                    String newId = matcher.group(1);
                    if (amount != 0 && !newId.equals(id)) {
                        throw new ParserException(errRoomListLiAFormat);
                    }
                    amount++;
                    id = newId;
                }
            }
            if (amount != 4) {
                throw new ParserException(errRoomListLiASizeNot4);
            }
            ids.add(id);
        }

        return ids;
    }
}
