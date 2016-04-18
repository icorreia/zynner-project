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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public class ReadersAndWritersTest {

    /** A logger for this class. */
    private static final Logger logger = LoggerFactory.getLogger(ReadersAndWritersTest.class);

    /**
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testReadAndWrite() throws IOException, InterruptedException {
        MessageReader<BasicMessage> reader = new MessageReader<>(12345, BasicMessage.class);
        MessageWriter<BasicMessage> writer = new MessageWriter<>("localhost", 12345, BasicMessage.class);

        reader.start();
        writer.start();

        writer.write(new BasicMessage<>("Hello world!"));
        writer.write(new BasicMessage<>("Hello world again!"));

        writer.stop();
        reader.stop();

        assertEquals("Reader should have received 2 messages.", 2, reader.getMessagesProcessed());
        assertEquals("Writer should have written 2 messages.", 2, writer.getMessagesProcessed());

        assertNotNull("First message should exist.", reader.getMessage());
        assertNotNull("Second message should exist.", reader.getMessage());
        assertNull("Third message should not exist", reader.getMessage(1, TimeUnit.SECONDS));
    }

    @Test
    public void testDifferentTypes() throws IOException, InterruptedException {
        MessageReader<BasicMessage> reader = new MessageReader<>(12345, BasicMessage.class);
        MessageWriter<BasicMessage> writer = new MessageWriter<>("localhost", 12345, BasicMessage.class);

        reader.start();
        writer.start();

        writer.write(new BasicMessage<>(42));
        writer.write(new BasicMessage<>(42.0));
        writer.write(new BasicMessage<>("String"));

        writer.stop();
        reader.stop();

        assertEquals("Reader should have received 3 messages.", 3, reader.getMessagesProcessed());
        assertEquals("Writer should have written 3 messages.", 3, writer.getMessagesProcessed());
    }
}
