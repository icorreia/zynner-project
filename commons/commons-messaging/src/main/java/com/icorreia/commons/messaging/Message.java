package com.icorreia.commons.messaging;

import java.io.Serializable;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public abstract class Message<T> implements Serializable {

    /** Serial Version UID */
    private static final long serialVersionUID = 1L;

    private T contents;

    public Message() {

    }

    public Message(T contents) {
        this.contents = contents;
    }

    public T getContents() {
        return contents;
    }
}
