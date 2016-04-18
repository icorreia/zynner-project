package com.icorreia.zmz;

import com.icorreia.commons.messaging.BasicMessage;
import com.icorreia.commons.messaging.Message;
import com.icorreia.zmz.readers.MessageReader;
import com.icorreia.zmz.writers.MessageWriter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public class ReadersAndWritersTest {

    /** A logger for this class. */
    private static final Logger logger = LoggerFactory.getLogger(ReadersAndWritersTest.class);


    @Test
    public void testReadAndWrite() throws IOException, InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(1);
        MessageReader<BasicMessage> reader = new MessageReader<>(12345, BasicMessage.class);
        MessageWriter<BasicMessage> writer = new MessageWriter<>("localhost", 12345, BasicMessage.class);

        (new Thread( () -> {
            reader.start(2);
            countDownLatch.countDown();
        })).start();

        writer.write(new BasicMessage<>("Hello world!"));
        writer.write(new BasicMessage<>("Hello world again!"));

        countDownLatch.await(10L, TimeUnit.SECONDS);

        assertEquals("Reader should have received 2 messages.", 2, reader.getMessagesProcessed());
        assertEquals("Writer should have written 2 messages.", 2, writer.getMessagesProcessed());
    }
}
