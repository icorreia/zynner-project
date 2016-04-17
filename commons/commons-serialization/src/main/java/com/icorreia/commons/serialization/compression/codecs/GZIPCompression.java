package com.icorreia.commons.serialization.compression.codecs;

import com.icorreia.commons.serialization.compression.CompressionCodec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public class GZIPCompression implements CompressionCodec {
    public InputStream compress(InputStream stream) throws IOException {
        return new GZIPInputStream(stream);
    }

    public OutputStream decompress(OutputStream stream) throws IOException {
        return new GZIPOutputStream(stream);
    }
}
