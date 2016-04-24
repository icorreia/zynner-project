package com.icorreia.zmz.readers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.icorreia.commons.messaging.BasicMessage;
import com.icorreia.commons.messaging.Message;
import com.icorreia.commons.serialization.Encoder;
import com.icorreia.zmz.Messenger;
import org.apache.commons.lang.Validate;
import org.objenesis.strategy.BaseInstantiatorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
    private Server server;

    /** Ths server listening port. */
    private int port;

    private LinkedBlockingQueue<T> messageQueue;

    private final Lock queueLock = new ReentrantLock();

    private final Condition notEmpty = queueLock.newCondition();

    private long commitLogSize;

    private String commitLogFolder;

    private CommitCleanupJob commitCleanupJob;

    private Thread cleanupThread;

    private Encoder<T> encoder;

    /**
     *
     * @param capacity
     * @param port
     * @param clazz
     */
    private MessageReader(int capacity, int port, long commitLogSize, String commitLogFolder, Class<T> clazz) {
        super(clazz);
        this.port = port;
        this.messageQueue = new LinkedBlockingQueue<>(capacity);
        this.commitLogFolder = commitLogFolder;
        this.commitLogSize = commitLogSize;

        this.server = new Server();
        this.encoder = new Encoder<>();
        this.encoder.registerClass(clazz);
    }

    @Override
    public void start() {
        try {
            server.start();
            server.bind(port);
            logger.info("Listening at port {}.", port);

            //TODO: Initial cleanup of non-empty folder.
            commitCleanupJob = new CommitCleanupJob();
            cleanupThread = new Thread(commitCleanupJob);
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
                        String commitLogFilename = commitLogFolder + File.separatorChar + System.currentTimeMillis();
                        try {
                            //FIXME
                            encoder.setOutput(commitLogFilename);
                            encoder.encode(message);
                            encoder.close();
                            logger.info("Created file '{}'.", commitLogFilename);
                            //TODO Maybe change the addition / name
                            commitCleanupJob.addFileName(commitLogFilename);
                        } catch (IOException e) {
                            logger.error("Could not create commit log.", e);
                        }
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
            stop();
        }
    }

    @Override
    public void stop() {
        try {
            encoder.close();
            cleanupThread.interrupt();
            cleanupThread.join();
            server.stop();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Interrupted while stopping.", e);
        }
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


    /***********************************************************************/

    public static class MessageReaderBuilder<T extends Message> {

        private Class<T> clazz;

        private int port;

        private int capacity = Integer.MAX_VALUE;

        private long commitLogSize;

        private String commitLogFolder = "src/main/java/test/resources";

        public static <T extends Message> MessageReaderBuilder<T> builder() {
            return new MessageReaderBuilder<>();
        }

        public MessageReaderBuilder<T> setPort(int port) {
            this.port = port;
            return this;
        }

        public MessageReaderBuilder<T> setCapacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public MessageReaderBuilder<T> setClass(Class<T> clazz) {
            this.clazz = clazz;
            return this;
        }

        public MessageReaderBuilder<T> setCommitLogSize(long commitLogSize) {
            this.commitLogSize = commitLogSize;
            return this;
        }

        public MessageReaderBuilder<T> setCommitLogFolder(String commitLogFolder) {
            this.commitLogFolder = commitLogFolder;
            return this;
        }

        public MessageReader<T> build() {
            return new MessageReader(capacity, port, commitLogSize, commitLogFolder, clazz);
        }

    }
}
