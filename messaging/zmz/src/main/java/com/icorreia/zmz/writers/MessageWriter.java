package com.icorreia.zmz.writers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.icorreia.commons.messaging.Message;
import com.icorreia.zmz.Messenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public class MessageWriter<T extends Message> extends Messenger {

    /** A logger for this class. */
    private static final Logger logger = LoggerFactory.getLogger(MessageWriter.class);

    /** */
    private final Client client;

    /** */
    private final int port;

    /** */
    private final String host;

    /**
     *
     *
     * @param host
     * @param port
     * @param clazz
     * @throws IOException
     */
    public MessageWriter(String host, int port, Class<T> clazz) throws IOException {
        super(clazz);
        this.client = new Client();
        this.port = port;
        this.host = host;
    }

    @Override
    public void start() {
        try {
            client.start();
            client.connect(5000, host, port);
            Kryo kryo = client.getKryo();
            kryo.register(clazz);

        } catch (IOException e) {
            logger.error("Error while writing to socket. ", e);
        }
    }

    @Override
    public void stop() {
        client.stop();
    }

    /**
     *
     *
     * @param message
     * @return
     */
    public int write(T message) {
        if (client.isConnected()) {
            int bytesSent = client.sendTCP(message);
            this.increaseMessagesProcessed();
            return bytesSent;
        } else {
            logger.warn("Server not connected. Run start() first.");
            return 0;
        }
    }
}
