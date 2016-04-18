package com.icorreia.zmz.readers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.icorreia.commons.messaging.BasicMessage;
import com.icorreia.commons.messaging.Message;
import com.icorreia.commons.serialization.Decoder;
import com.icorreia.zmz.Messenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public class MessageReader<T extends Message> extends Messenger {

    /** A logger for this class. */
    private static final Logger logger = LoggerFactory.getLogger(MessageReader.class);

    /** */
    private final Server server;

    /** */
    private final int port;

    /**
     *
     *
     * @param port
     * @param clazz
     */
    public MessageReader(int port, Class<T> clazz) {
        super(clazz);
        this.server = new Server();
        this.port = port;
    }

    @Override
    public void start() {
        try {
            server.start();
            server.bind(port);
            logger.info("Listening at port {}.", port);

            Kryo kryo = server.getKryo();
            kryo.register(clazz);

            final MessageReader reader = this;

            server.addListener(new Listener() {
                public void received (Connection connection, Object object) {
                    if (clazz.isAssignableFrom(object.getClass())) {
                        T message = (T) object;
                        logger.info("Received: ''{}.", message.getContents());
                        reader.increaseMessagesProcessed();
                    } else {
                        logger.warn("Object's class '{}' is not assignable to defined reader class '{}'.",
                                object.getClass(), clazz.getClass());
                    }
                }
            });

        } catch (IOException e) {
            logger.error("Exception while reading from socket: ", e);
            server.close();
        }
    }

    @Override
    public void stop() {
        server.stop();
    }
}
