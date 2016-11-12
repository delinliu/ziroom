package http_fetcher;

import org.junit.Assert;
import org.junit.Test;

public class HttpFetcherExceptionTest {

    static final String normalUrl = "http://sh.ziroom.com/";
    static final String notFoundUrl = "http://sh.ziroom.com/notfound";
    static final String malformedUrl = "malformed url";
    static final String cannotOpenUrl = "http://something.cannot.open";

    @Test
    public void testFetchContentSuccess() throws HttpFetcherException {
        SimpleHttpFetcher fetcher = new SimpleHttpFetcher();
        Assert.assertNotNull(fetcher.fetchContent(normalUrl));
    }

    @Test
    public void testFetchContentExceptionMalformedURL() {
        SimpleHttpFetcher fetcher = new SimpleHttpFetcher();
        try {
            fetcher.fetchContent(malformedUrl);
            Assert.assertTrue(false);
        } catch (HttpFetcherException e) {
            Assert.assertEquals("Malformed URL", e.getMessage());
        }
    }

    @Test
    public void testFetchContentExceptionNotFoundURL() {
        SimpleHttpFetcher fetcher = new SimpleHttpFetcher();
        try {
            fetcher.fetchContent(notFoundUrl);
            Assert.assertTrue(false);
        } catch (HttpFetcherException e) {
            Assert.assertEquals("Response Code is 404", e.getMessage());
        }
    }

    @Test
    public void testFetchContentExceptionCannotOpenURL() {
        SimpleHttpFetcher fetcher = new SimpleHttpFetcher();
        try {
            fetcher.fetchContent(cannotOpenUrl);
            Assert.assertTrue(false);
        } catch (HttpFetcherException e) {
            Assert.assertEquals("Cannot open URL", e.getMessage());
        }
    }
}
