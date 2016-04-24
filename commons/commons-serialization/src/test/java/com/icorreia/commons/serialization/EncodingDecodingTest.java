package com.icorreia.commons.serialization;

import com.icorreia.commons.messaging.BasicMessage;
import com.icorreia.commons.messaging.Message;
import org.junit.Test;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.nio.file.Files;
import java.nio.file.Path;

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
    public void encodeAndDecodeRegisteredBasicMessage() throws IOException {
        Path commitLog = Files.createTempFile("commit_log", "encodeAndDecodeRegisteredBasicMessage");
        Encoder<BasicMessage> encoder = new Encoder<>();
        Decoder<BasicMessage> decoder = new Decoder<>();

        encoder.registerClass(BasicMessage.class);
        decoder.registerClass(BasicMessage.class);
        encoder.setOutput(commitLog.toString());
        decoder.setInput(commitLog.toString());

        BasicMessage message = new BasicMessage("Hello there!");
        encoder.encode(message);

        assertEquals("Original contents and encoded-decoded contents must match.", message.getContents(), decoder.decode(BasicMessage.class).getContents());
    }

    /**
     * Tests if the processing of encoding and decoding is working for a {@link BasicMessage<String>},
     * when it is not registered in Kryo.
     */
    @Test
    public void encodeAndDecodeUnregisteredBasicMessage() throws IOException {
        Path commitLog = Files.createTempFile("commit_log", "encodeAndDecodeUnregisteredBasicMessage");
        Encoder<BasicMessage> encoder = new Encoder<>();
        Decoder<BasicMessage> decoder = new Decoder<>();

        encoder.setOutput(commitLog.toString());
        decoder.setInput(commitLog.toString());

        BasicMessage message = new BasicMessage("Hello there!");
        encoder.encode(message);

        assertEquals("Original contents and encoded-decoded contents must match.", message.getContents(), decoder.decode(BasicMessage.class).getContents());
    }

    /**
     * Tests if the processing of encoding and decoding is working for a {@link String},
     * when it is registered in Kryo.
     */
    @Test
    public void encodeAndDecodeRegisteredString() throws IOException {
        Path commitLog = Files.createTempFile("commit_log", "encodeAndDecodeRegisteredString");
        Encoder<String> encoder = new Encoder<>();
        Decoder<String> decoder = new Decoder<>();

        encoder.registerClass(String.class);
        decoder.registerClass(String.class);
        encoder.setOutput(commitLog.toString());
        decoder.setInput(commitLog.toString());

        String contents = "Hello there!";
        encoder.encode(contents);

        assertEquals("Original contents and encoded-decoded contents must match.", contents, decoder.decode(String.class));
    }

    /**
     * Tests if the processing of encoding and decoding is working for a {@link String},
     * when it is not registered in Kryo.
     */
    @Test
    public void encodeAndDecodeUnregisteredString() throws IOException {
        Path commitLog = Files.createTempFile("commit_log", "encodeAndDecodeUnregisteredString");
        Encoder<String> encoder = new Encoder<>();
        Decoder<String> decoder = new Decoder<>();

        encoder.setOutput(commitLog.toString());
        decoder.setInput(commitLog.toString());

        String contents = "Hello there!";
        encoder.encode(contents);

        assertEquals("Original contents and encoded-decoded contents must match.", contents, decoder.decode(String.class));
    }

    /**
     *
     */
    @Test
    public void encodeAndDecodeMultipleObjectsSameClass() throws IOException {
        Path commitLog = Files.createTempFile("commit_log", "encodeAndDecodeUnregisteredString");
        Encoder<String> encoder = new Encoder<>();
        Decoder<String> decoder = new Decoder<>();

        encoder.registerClass(String.class);
        decoder.registerClass(String.class);
        encoder.setOutput(commitLog.toString());
        decoder.setInput(commitLog.toString());

        String contents1 = "Hello there!";
        encoder.encode(contents1);
        String contents2 = "Hello there too!";
        encoder.encode(contents2);

        assertEquals("Original contents and encoded-decoded contents must match.", contents1, decoder.decode(String.class));
        assertEquals("Original contents and encoded-decoded contents must match.", contents2, decoder.decode(String.class));
    }

    /**
     *
     */
    @Test
    public void encodeAndDecodeMultipleObjectsDifferentClass() throws IOException {
        Path commitLog = Files.createTempFile("commit_log", "encodeAndDecodeUnregisteredString");
        Encoder<Message> encoder = new Encoder<>();
        Decoder<Message> decoder = new Decoder<>();

        encoder.registerClass(BasicMessage.class);
        encoder.registerClass(DummyMessage.class);
        encoder.registerClass(Message.class);
        decoder.registerClass(BasicMessage.class);
        decoder.registerClass(DummyMessage.class);
        decoder.registerClass(Message.class);

        encoder.setOutput(commitLog.toString());
        decoder.setInput(commitLog.toString());

        Message dummyMessage = new DummyMessage("Hello there!");
        encoder.encodeWithClass(dummyMessage);
        Message message = new BasicMessage("Hello there too!");
        encoder.encodeWithClass(message);

        assertEquals("Original contents and encoded-decoded contents must match.", dummyMessage.getContents(), decoder.decodeWithClass().getContents());
        assertEquals("Original contents and encoded-decoded contents must match.", message.getContents(), decoder.decodeWithClass().getContents());
    }
}
