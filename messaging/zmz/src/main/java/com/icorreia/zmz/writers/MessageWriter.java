package com.icorreia.zmz.writers;

import com.icorreia.commons.messaging.Message;
import com.icorreia.commons.serialization.Encoder;
import com.icorreia.zmz.MessageStatistic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public class MessageWriter<T extends Message> extends MessageStatistic {

    /** A logger for this class. */
    private static final Logger logger = LoggerFactory.getLogger(MessageWriter.class);

    private final Socket outgoing;

    private final Encoder<T> encoder;

    private final int port;

    private final String host;

    public MessageWriter(String host, int port, Class<T> clazz) throws IOException {
        this.outgoing = new Socket();
        this.encoder = new Encoder<>(clazz);
        this.port = port;
        this.host = host;

        outgoing.connect(new InetSocketAddress(host , port));
    }

    public MessageWriter(String host, int port) throws IOException {
        this.outgoing = new Socket();
        this.encoder = new Encoder<>();
        this.port = port;
        this.host = host;

        outgoing.connect(new InetSocketAddress(host , port));
    }


    public void write(T message) {
        try
        {
            outgoing.getOutputStream().write(encoder.encode(message));
            increaseMessagesProcessed();

        } catch (UnknownHostException e) {
            logger.error("Could not find host '{}:{}'.", host, port, e);
        } catch (IOException e) {
            logger.error("Error while writing to socket. ", e);
        }
    }
}
