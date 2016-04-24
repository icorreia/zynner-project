package com.icorreia.commons.messaging;

import java.io.Serializable;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public abstract class Message implements Serializable {

    /** Serial Version UID */
    private static final long serialVersionUID = 1L;

    private Object contents;

    public Message() {

    }

    public Message(Object contents) {
        this.contents = contents;
    }

    public Object getContents() {
        return contents;
    }
}
