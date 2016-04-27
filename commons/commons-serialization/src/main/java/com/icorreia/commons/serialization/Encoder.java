package com.icorreia.commons.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.OutputChunked;
import com.icorreia.commons.serialization.compression.CompressionCodec;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public class Encoder<T extends Serializable> implements Closeable {

    /** */
    private CompressionCodec compressionCodec;

    /** */
    private Kryo kryo;

    /** */
    private OutputChunked output;

    /**
     *
     */
    public Encoder() {
        kryo = new Kryo();
    }

    /**
     *
     * @param newClazz
     */
    public void registerClass(Class<? extends T> newClazz) {
        kryo.setRegistrationRequired(true);
        kryo.register(newClazz);
    }

    /**
     *
     * @param filename
     * @throws IOException
     */
    public void setOutput(String filename) throws IOException {
        if (output != null) {
            output.close();
        }

        output = new OutputChunked(new FileOutputStream(filename));
    }

    /**
     *
     * Must be used along with {@link Decoder#decode(Class<? extends T>)}.
     *
     * @param object
     * @return
     */
    public long encode(T object) {
        output.clear();
        kryo.writeObject(output, object);
        output.endChunks();
        return output.total();
    }

    /**
     *
     * Must be used along with {@link Decoder#decodeWithClass()}.
     *
     * @param object
     * @return
     */
    public long encodeWithClass(Object object) {
        output.clear();
        kryo.writeClassAndObject(output, object);
        output.endChunks();
        return output.total();
    }

    /**
     *
     */
    public void close() {
        if (output != null) {
            output.close();
        }
    }
}
