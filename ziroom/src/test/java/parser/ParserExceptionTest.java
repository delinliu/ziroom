package parser;

import org.junit.Assert;
import org.junit.Test;

public class ParserExceptionTest {

    @Test
    public void test() {
        try {
            throw new ParserException("abcd");
        } catch (ParserException e) {
            Assert.assertEquals("abcd", e.getBaseMessage());
        }

        try {
            throw new ParserException("abcd", "12345");
        } catch (ParserException e) {
            Assert.assertEquals("abcd", e.getBaseMessage());
            Assert.assertEquals("abcd [roomId=12345]", e.getMessage());
        }
    }
}
