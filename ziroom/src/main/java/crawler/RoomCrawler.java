package crawler;

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import concurrent_set.ConcurrentSetInterface;
import entity.Room;
import http_fetcher.HttpFetcher;
import http_fetcher.HttpFetcherException;
import http_fetcher.SimpleHttpFetcher;
import parser.ParserException;
import parser.RoomParser;
import parser.RoomParserInterface;

public class RoomCrawler {

    private HttpFetcher httpFetcher = new SimpleHttpFetcher();
    private RoomParserInterface parser = new RoomParser();
    private BlockingQueue<Room> roomQueue;
    private ConcurrentSetInterface idSet;
    private ExecutorService executor;
    private boolean isRunning = false;
    private int sleepSecond;

    private String url = "http://sh.ziroom.com/z/vr/%s.html";

    public RoomCrawler(ConcurrentSetInterface idSet, BlockingQueue<Room> roomQueue) {
        this.idSet = idSet;
        this.roomQueue = roomQueue;
    }

    public void startCrawler(int threadAmount, int sleepSecond) {
        if (isRunning) {
            return;
        }

        this.sleepSecond = sleepSecond;
        executor = Executors.newFixedThreadPool(threadAmount);
        for (int i = 0; i < threadAmount; i++) {
            executor.execute(new CrawlerHelper());
        }
        isRunning = true;
    }

    public void stopCrawler() {
        if (!isRunning) {
            return;
        }
        executor.shutdown();
        isRunning = false;
    }

    private class CrawlerHelper extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    String id = idSet.next();
                    String roomPage = String.format(url, id);
                    String content = httpFetcher.fetchContent(roomPage);
                    Room room = parser.parseRoom(content);
                    roomQueue.put(room);
                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                } catch (HttpFetcherException e) {
                    e.printStackTrace();
                } catch (ParserException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sleep(sleepSecond);
            }
        }

        private void sleep(int second) {
            try {
                Thread.sleep(second * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
