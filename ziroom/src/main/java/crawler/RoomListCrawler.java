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
        System.out.println("Room list crawler started. [threadAmount=" + threadAmount + ", sleepSecond=" + sleepSecond
                + ", resetSecond=" + resetSecond + "]");
    }

    public void stopCrawler() {
        if (!isRunning) {
            return;
        }
        executor.shutdownNow();
        isRunning = false;
        System.out.println("Room list crawler stoped.");
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
                    System.out.println("Crawled page " + page + ".");
                } catch (HttpFetcherException e) {
                    e.printStackTrace();
                } catch (ParserException e) {
                    if (RoomListParser.errRoomListNoMore.equals(e.getMessage())) {
                        try {
                            Thread.sleep(resetSecond * 1000);
                        } catch (InterruptedException ee) {
                            // e.printStackTrace();
                            break;
                        }
                        currentPage.set(1);
                        System.out.println("Page " + page + " not found, go to first page.");
                    } else {
                        System.err.println("ParserException, url=" + url);
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(sleepSecond * 1000);
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                    break;
                }
            }
            System.out.println("Room list crawler quit.");
        }
    }
}
