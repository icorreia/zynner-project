package com.icorreia.commons.messaging;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public class BasicMessage<T> extends Message<T> {

    /** Serial Version UID */
    private static final long serialVersionUID = 1L;

    public BasicMessage() {

    }

    public BasicMessage(T contents) {
        super(contents);
    }

}
