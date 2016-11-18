package crawler;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import concurrent_set.ConcurrentSetInterface;
import http_fetcher.HttpFetcher;
import http_fetcher.HttpFetcherException;
import http_fetcher.SimpleHttpFetcher;
import parser.ParserException;
import parser.RoomListParser;
import parser.RoomListParserInterface;

public class RoomListCrawler {

    private String roomListUrl;
    private AtomicInteger currentPage = new AtomicInteger(1);
    private ConcurrentSetInterface idSet;
    private int sleepSecond;
    private int resetSecond;

    private HttpFetcher httpFetcher = new SimpleHttpFetcher();
    private RoomListParserInterface parser = new RoomListParser();

    private ExecutorService executor;
    private boolean isRunning = false;

    public RoomListCrawler(ConcurrentSetInterface idSet) {
        this.idSet = idSet;
    }

    public void startCrawler(int threadAmount, int sleepSecond, int resetSecond, String roomListUrl) {
        if (isRunning) {
            return;
        }

        this.sleepSecond = sleepSecond;
        this.resetSecond = resetSecond;
        this.roomListUrl = roomListUrl;
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

    public String getRoomListUrl() {
        return roomListUrl;
    }

    public int getCurrentPage() {
        return currentPage.get();
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage.set(currentPage);
    }

    private class CrawlerHelper extends Thread {
        @Override
        public void run() {
            while (true) {
                int page = currentPage.getAndIncrement();
                String url = roomListUrl + "?p=" + page;
                try {
                    String content = httpFetcher.fetchContent(url);
                    Set<String> ids = parser.parseRoomList(content);
                    idSet.addAll(ids);
                } catch (HttpFetcherException e) {
                    e.printStackTrace();
                } catch (ParserException e) {
                    e.printStackTrace();
                    if (RoomListParser.errRoomListNoMore.equals(e.getMessage())) {
                        sleep(resetSecond);
                        currentPage.set(1);
                    }
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
