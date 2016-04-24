package com.icorreia.commons.messaging;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public class BasicMessage extends Message {

    /** Serial Version UID */
    private static final long serialVersionUID = 1L;

    public BasicMessage() {

    }

    public BasicMessage(Object contents) {
        super(contents);
    }

}
