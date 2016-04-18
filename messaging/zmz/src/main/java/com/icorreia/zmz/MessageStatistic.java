package com.icorreia.zmz;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public abstract class MessageStatistic {

    private long messagesProcessed;

    protected void increaseMessagesProcessed() {
        messagesProcessed++;
    }

    protected void increaseMessagesProcessed(long value) {
        messagesProcessed += value;
    }

    public long getMessagesProcessed() {
        return messagesProcessed;
    }

}
