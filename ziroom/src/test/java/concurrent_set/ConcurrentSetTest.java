package concurrent_set;

import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

public class ConcurrentSetTest {

    @Test
    public void testEmpty() {

        ConcurrentSetInterface set = new ConcurrentSet();

        try {
            set.next();
            Assert.assertTrue(false);
        } catch (NoSuchElementException e) {
            // empty
        }
    }

    @Test
    public void testMutiThread() throws InterruptedException {

        ConcurrentSetInterface set = new ConcurrentSet();
        set.add("123");
        set.add("abc");

        int threadAmount = 100;
        Executor executor = Executors.newFixedThreadPool(threadAmount);
        AtomicInteger successAmount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(threadAmount);
        for (int i = 0; i < threadAmount; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < 1000; i++) {
                            set.next();
                        }
                        successAmount.incrementAndGet();
                    } catch (NoSuchElementException e) {
                        // empty
                    }
                    latch.countDown();
                }
            });
        }

        latch.await();
        Assert.assertEquals(threadAmount, successAmount.get());
        Assert.assertEquals("123abc", set.next() + set.next());
    }
}
