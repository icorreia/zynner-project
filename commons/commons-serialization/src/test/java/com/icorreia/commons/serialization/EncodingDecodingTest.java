package com.icorreia.commons.serialization;

import com.icorreia.commons.messaging.BasicMessage;
import com.icorreia.commons.messaging.Message;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public class EncodingDecodingTest {

    @Test
    public void encodeAndDecode() {
        Encoder<BasicMessage<String>> encoder = new Encoder<>();

        BasicMessage<String> message = new BasicMessage<>("Hello there!");
        byte[] encodedData = encoder.encode(message);

        assertEquals("", message.getContents(), encoder.decode(encodedData).getContents());
    }
}
