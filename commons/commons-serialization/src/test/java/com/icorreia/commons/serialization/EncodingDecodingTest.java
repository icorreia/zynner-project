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

    /**
     * Tests if the processing of encoding and decoding is working for a {@link BasicMessage<String>}.
     */
    @Test
    public void encodeAndDecodeBasicMessage() {
        Encoder<BasicMessage<String>> encoder = new Encoder<>();

        BasicMessage<String> message = new BasicMessage<>("Hello there!");
        byte[] encodedData = encoder.encode(message);

        assertEquals("Original contents and encoded-decoded contents must match.", message.getContents(), encoder.decode(encodedData).getContents());
    }

    /**
     * Tests if the processing of encoding and decoding is working for a {@link String}.
     */
    @Test
    public void encodeAndDecodeBasicString() {
        Encoder<String> encoder = new Encoder<>();

        String contents = "Hello there!";
        byte[] encodedData = encoder.encode(contents);

        assertEquals("Original contents and encoded-decoded contents must match.", contents, encoder.decode(encodedData));
    }
}
