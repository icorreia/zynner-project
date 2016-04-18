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
     * Tests if the processing of encoding and decoding is working for a {@link BasicMessage<String>},
     * when it is registered in Kryo.
     */
    @Test
    public void encodeAndDecodeRegisteredBasicMessage() {
        Encoder<BasicMessage<String>> encoder = new Encoder<>(BasicMessage.class);
        Decoder<BasicMessage<String>> decoder = new Decoder<>(BasicMessage.class);

        BasicMessage<String> message = new BasicMessage<>("Hello there!");
        byte[] encodedData = encoder.encode(message);

        assertEquals("Original contents and encoded-decoded contents must match.", message.getContents(), decoder.decode(encodedData).getContents());
    }

    /**
     * Tests if the processing of encoding and decoding is working for a {@link BasicMessage<String>},
     * when it is not registered in Kryo.
     */
    @Test
    public void encodeAndDecodeUnregisteredBasicMessage() {
        Encoder<BasicMessage<String>> encoder = new Encoder<>();
        Decoder<BasicMessage<String>> decoder = new Decoder<>();

        BasicMessage<String> message = new BasicMessage<>("Hello there!");
        byte[] encodedData = encoder.encode(message);

        assertEquals("Original contents and encoded-decoded contents must match.", message.getContents(), decoder.decode(encodedData).getContents());
    }

    /**
     * Tests if the processing of encoding and decoding is working for a {@link String},
     * when it is registered in Kryo.
     */
    @Test
    public void encodeAndDecodeRegisteredString() {
        Encoder<String> encoder = new Encoder<>(String.class);
        Decoder<String> decoder = new Decoder<>(String.class);

        String contents = "Hello there!";
        byte[] encodedData = encoder.encode(contents);

        assertEquals("Original contents and encoded-decoded contents must match.", contents, decoder.decode(encodedData));
    }

    /**
     * Tests if the processing of encoding and decoding is working for a {@link String},
     * when it is not registered in Kryo.
     */
    @Test
    public void encodeAndDecodeUnregisteredString() {
        Encoder<String> encoder = new Encoder<>();
        Decoder<String> decoder = new Decoder<>();

        String contents = "Hello there!";
        byte[] encodedData = encoder.encode(contents);

        assertEquals("Original contents and encoded-decoded contents must match.", contents, decoder.decode(encodedData));
    }
}
