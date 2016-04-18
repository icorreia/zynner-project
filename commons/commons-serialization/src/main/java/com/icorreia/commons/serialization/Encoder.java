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
public class Encoder<T extends Serializable> {

    private CompressionCodec compressionCodec;

    private Kryo kryo;

    public Encoder() {
        kryo = new Kryo();
    }

    public Encoder(Class<?> clazz) {
        kryo = new Kryo();
        kryo.setRegistrationRequired(true);
        kryo.register(clazz);
    }

    public byte[] encode(T object) {
        byte [] encodedData = SerializationUtils.serialize(object);
        Output output = new Output(encodedData);
        kryo.writeClassAndObject(output, object);
        output.close();

        return encodedData;
    }
}
