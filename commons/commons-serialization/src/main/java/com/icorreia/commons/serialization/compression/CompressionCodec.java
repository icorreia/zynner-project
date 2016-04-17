package com.icorreia.commons.serialization.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public interface CompressionCodec {

    InputStream compress(InputStream stream) throws IOException;

    OutputStream decompress(OutputStream stream) throws IOException;
}
