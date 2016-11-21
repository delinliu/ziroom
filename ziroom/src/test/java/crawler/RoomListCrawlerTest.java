package crawler;

import org.junit.Assert;
import org.junit.Test;

import concurrent_set.ConcurrentSet;
import concurrent_set.ConcurrentSetInterface;

public class RoomListCrawlerTest {

    static private final String roomListUrl = "http://sh.ziroom.com/z/nl/z2-s6%E5%8F%B7%E7%BA%BF.html";

    @Test
    public void test() throws InterruptedException {
        ConcurrentSetInterface set = new ConcurrentSet();
        RoomListCrawler crawler = new RoomListCrawler(set);
        crawler.startCrawler(3, 1, 3, roomListUrl);
        int second = 15;
        System.out.println("Wait " + second + "s for room list crawler test.");
        Thread.sleep(second * 1000);
        crawler.stopCrawler();
        Assert.assertTrue(set.size() > 100);
    }
}
