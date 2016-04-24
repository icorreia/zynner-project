package com.icorreia.commons.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.OutputChunked;
import com.icorreia.commons.serialization.compression.CompressionCodec;

import java.io.*;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public class Encoder<T extends Serializable> {

    private CompressionCodec compressionCodec;

    private Kryo kryo;

    private OutputChunked output;

    public Encoder() {
        kryo = new Kryo();
    }

    public void registerClass(Class<? extends T> newClazz) {
        kryo.setRegistrationRequired(true);
        kryo.register(newClazz);
    }

    public void setOutput(String filename) throws IOException {
        if (output != null) {
            output.close();
        }

        output = new OutputChunked(new FileOutputStream(filename));
    }

    public void encode(T object) {
        kryo.writeObject(output, object);
        output.endChunks();
    }

    public void encodeWithClass(Object object) {
        kryo.writeClassAndObject(output, object);
        output.endChunks();
    }

    public void close() {
        if (output != null) {
            output.close();
        }
    }
}
