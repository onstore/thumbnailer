package org.geoint.thumbnailer.converter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Converts media from its format to a BufferedImage.
 *
 * MediaConverter instances MUST be thread-safe.
 *
 * MediaConvert instances MUST be registered with the ServiceLoader to be
 * discovered.
 */
public interface MediaConverter {

    /**
     * Returns the supported media types of this converter.
     *
     * @return
     */
    String[] supportedMediaTypes();

    /**
     * Converts the media to a BufferedImage. This conversion process must be
     * thread-safe.
     *
     * @param in
     * @return
     * @throws java.io.IOException
     */
    BufferedImage convert(InputStream in) throws IOException;

}
