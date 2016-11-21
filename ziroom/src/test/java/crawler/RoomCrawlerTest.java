package crawler;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.Test;

import concurrent_set.ConcurrentSet;
import concurrent_set.ConcurrentSetInterface;
import entity.Room;

public class RoomCrawlerTest {

    @Test
    public void test() throws InterruptedException {

        ConcurrentSetInterface set = new ConcurrentSet();
        set.addAll(Arrays.asList(new String[] { "60126378", "60280613", "60298387", "305928", "60196598", "60312658",
                "60314279", "60298388" }));
        BlockingQueue<Room> roomQueue = new ArrayBlockingQueue<>(1000);
        RoomCrawler crawler = new RoomCrawler(set, roomQueue);
        crawler.startCrawler(1, 1);

        int second = 10;
        System.out.println("Wait " + second + "s for room crawler test.");
        Thread.sleep(second * 1000);
        crawler.stopCrawler();
    }
}
