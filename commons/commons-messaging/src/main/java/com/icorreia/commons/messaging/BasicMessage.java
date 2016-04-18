package com.icorreia.commons.messaging;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public class BasicMessage<T> implements Message<T> {

    /** Serial Version UID */
    private static final long serialVersionUID = 1L;

    private T contents;


    public BasicMessage(T contents) {
        this.contents = contents;
    }

    public T getContents() {
        return contents;
    }

}
