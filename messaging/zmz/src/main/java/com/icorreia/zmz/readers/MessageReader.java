package com.icorreia.zmz.readers;

import com.icorreia.commons.messaging.Message;
import com.icorreia.commons.serialization.Decoder;
import com.icorreia.zmz.MessageStatistic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public class MessageReader<T extends Message> extends MessageStatistic {

    /** A logger for this class. */
    private static final Logger logger = LoggerFactory.getLogger(MessageReader.class);

    private final ServerSocket incomingSoket;

    private final Decoder<Message> decoder;

    public MessageReader(int port, Class<T> clazz) throws IOException {
        incomingSoket = new ServerSocket(port);
        decoder = new Decoder<>(clazz);
    }

    public MessageReader(int port) throws IOException {
        incomingSoket = new ServerSocket(port);
        decoder = new Decoder<>();
    }

    public void start(final int maxMessages) {
        int counter = 0;

        try (Socket conn = incomingSoket.accept()) {
            logger.info("Connection received from " + conn.getInetAddress().getHostName() + " : " + conn.getPort());

            do {
                logger.info("Reading message...");
                byte[] dataBuffer = new byte[150];
                ObjectInputStream ois = new ObjectInputStream(conn.getInputStream());
                ois.readObject();
                Message message = decoder.decode(dataBuffer);
                logger.info("Received message: {}", message.getContents());

                counter++;
                increaseMessagesProcessed();
            }
            while (counter != maxMessages);

        } catch (IOException e) {
            logger.error("Exception while reading from socket: ", e);
        }
    }
}
