package com.icorreia.zmz.readers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.icorreia.commons.messaging.Message;
import com.icorreia.zmz.Messenger;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    /** Ths server listening port. */
    private final int port;

    private final LinkedBlockingQueue<T> messageQueue;

    private final Lock queueLock = new ReentrantLock();

    private final Condition notEmpty = queueLock.newCondition();

    private String commitLogFolder = "src";

    private CommitCleanupJob commitCleanupJob;

    /**
     *
     *
     * @param port
     * @param clazz
     */
    public MessageReader(int port, Class<T> clazz) {
        this(Integer.MAX_VALUE, port, clazz);
    }

    /**
     *
     * @param capacity
     * @param port
     * @param clazz
     */
    public MessageReader(int capacity, int port, Class<T> clazz) {
        super(clazz);
        this.server = new Server();
        this.port = port;
        this.messageQueue = new LinkedBlockingQueue<>(capacity);

        this.commitCleanupJob = new CommitCleanupJob(commitLogFolder);
    }

    @Override
    public void start() {
        try {
            server.start();
            server.bind(port);
            logger.info("Listening at port {}.", port);

            //TODO: Initial cleanup of non-empty folder.
            Thread cleanupThread = new Thread(commitCleanupJob);
            cleanupThread.start();

            Kryo kryo = server.getKryo();
            kryo.register(clazz);

            final MessageReader reader = this;

            server.addListener(new Listener() {
                public void received (Connection connection, Object object) {
                    if (clazz.isAssignableFrom(object.getClass())) {
                        T message = (T) object;
                        logger.trace("Received: '{}'.", message.getContents());
                        messageQueue.add(message);
                        queueLock.lock();
                        notEmpty.signalAll();
                        queueLock.unlock();
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

    /**
     * Invokes {@link #getMessage(long, TimeUnit)} with {@code time} set to 0.
     *
     * @return  an element from the message queue or null
     * @throws InterruptedException
     */
    public T getMessage() throws InterruptedException{
        return getMessage(0, null);
    }

    /**
     *
     *
     * @param time
     * @param unit
     * @return  an element from the message queue or null
     * @throws InterruptedException  if the thread is interrupted while blocked in the await() call
     */
    public T getMessage(long time, TimeUnit unit) throws InterruptedException{
        Validate.isTrue(time >= 0);
        try {
            queueLock.lock();
            while (messageQueue.isEmpty()) {
                if (time == 0) {
                    notEmpty.await();
                } else {
                    if (!notEmpty.await(time, unit)) {
                        return null;
                    }
                }
            }
            return messageQueue.poll();
        } finally {
            queueLock.unlock();
        }
    }
}
