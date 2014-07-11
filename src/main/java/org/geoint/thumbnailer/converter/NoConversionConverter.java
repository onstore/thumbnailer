package org.geoint.thumbnailer.converter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * A MediaConverter that does no conversion. This converter is used for native
 * images supported by ImageIO.
 */
public class NoConversionConverter implements MediaConverter {

    @Override
    public String[] supportedMediaTypes() {
        return ImageIO.getReaderMIMETypes();
    }

    @Override
    public BufferedImage convert(InputStream in) throws IOException {
        return ImageIO.read(in);
    }

}
