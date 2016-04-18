package com.icorreia.commons.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.icorreia.commons.serialization.compression.CompressionCodec;
import org.apache.commons.lang.SerializationUtils;

import java.io.*;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public class Decoder<T extends Serializable> {

    private CompressionCodec compressionCodec;

    private Kryo kryo;

    public Decoder() {
        kryo = new Kryo();
    }

    public Decoder(Class<?> clazz) {
        kryo = new Kryo();
        kryo.setRegistrationRequired(true);
        kryo.register(clazz);
    }

    public T decode(byte[] data) {
        Input input = new Input(data);
        T decodedData = (T) kryo.readClassAndObject(input);
        input.close();

        return decodedData;
    }
}
