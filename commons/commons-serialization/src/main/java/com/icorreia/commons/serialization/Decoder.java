package com.icorreia.commons.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.InputChunked;
import com.icorreia.commons.serialization.compression.CompressionCodec;

import java.io.*;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public class Decoder<T extends Serializable> {

    private CompressionCodec compressionCodec;

    private Kryo kryo;

    private InputChunked input;

    public Decoder() {
        kryo = new Kryo();
    }

    public void registerClass(Class<? extends T> newClazz) {
        kryo.setRegistrationRequired(true);
        kryo.register(newClazz);
    }

    public void setInput(String filename) throws IOException {
        if (input != null) {
            input.close();
        }
        input = new InputChunked(new FileInputStream(filename));
    }

    public T decode(Class<? extends T> clazz) {
        T decodedObject = kryo.readObject(input, clazz);
        input.nextChunks();

        return decodedObject;
    }

    public T decodeWithClass() {
        T decodedObject = (T) kryo.readClassAndObject(input);
        input.nextChunks();

        return decodedObject;
    }

    public void close() {
        if (input != null) {
            input.close();
        }
    }
}
