package com.icorreia.zmz;

import com.icorreia.commons.messaging.Message;

/**
 * The skeleton for the messaging components.
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public abstract class Messenger<T extends Message> {

    /** The number of messages processed by this component. */
    protected long messagesProcessed;

    /** The class of messages accepted by this component. */
    protected Class<T> clazz;

    /**
     * The class must be explicitly passed to register it in the Kryo {@link com.esotericsoftware.kryonet.Server},
     * as generic types cannot be directly registered.
     *
     * @param clazz  the class of messages accepted by this component
     */
    public Messenger(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Increases by 1 the number of messages processed.
     */
    protected void increaseMessagesProcessed() {
        messagesProcessed++;
    }

    /**
     * Increases by {@code value} the number of messages processed.
     *
     * @param value  the increment on number of messages processed.
     */
    protected void increaseMessagesProcessed(long value) {
        messagesProcessed += value;
    }

    /**
     * Get the number of messages processed.
     *
     * @return  the number of messages processed
     */
    public long getMessagesProcessed() {
        return messagesProcessed;
    }

    /**
     * Start the server.
     */
    public abstract void start();

    /**
     * Stop the server.
     */
    public abstract void stop();

    /**
     * Restart the server, by calling {@link #stop()} and {@link #start()}.
     */
    public void restart() {
        stop();
        start();
    }

}
