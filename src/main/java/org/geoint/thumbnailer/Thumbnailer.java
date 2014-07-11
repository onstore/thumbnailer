package org.geoint.thumbnailer;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.geoint.thumbnailer.converter.MediaConverter;

/**
 * Creates image thumbnails for media.
 *
 */
public class Thumbnailer {

    private final static Map<String, MediaConverter> MEDIA_CONVERTERS; //key is media type
    private final static Set<String> ACCEPTABLE_IMG_FORMATS;

    public final static String DEFAULT_OUTPUT_FORMAT = "jpeg";

    static {
        //setup the converter index
        MEDIA_CONVERTERS = new HashMap<>();
        ServiceLoader<MediaConverter> loader = ServiceLoader.load(MediaConverter.class);
        for (MediaConverter c : loader) {
            for (String mt : c.supportedMediaTypes()) {
                MEDIA_CONVERTERS.put(mt, c);
            }
        }

        //setup the image format index
        ACCEPTABLE_IMG_FORMATS = new HashSet<>(Arrays.asList(ImageIO.getWriterFormatNames()));
    }

    public static Builder thumb(File file)
            throws IOException, FileNotFoundException {
        TikaInputStream in
                = TikaInputStream.get(
                        new BufferedInputStream(
                                new FileInputStream(file)));
        MediaConverter converter = getConverter(getContentType(in, file.getName()));
        return new ThumbnailatorBuilder(in, converter);

    }

    public static Builder thumb(InputStream in) throws IOException {
        TikaInputStream tin = TikaInputStream.get(in);
        MediaConverter converter = getConverter(getContentType(tin, null));
        return new ThumbnailatorBuilder(tin, converter);
    }

    public static Builder thumb(URL url) throws IOException {
        TikaInputStream in = TikaInputStream.get(
                new BufferedInputStream(url.openStream()));
        MediaConverter converter = getConverter(getContentType(in, url.getFile()));
        return new ThumbnailatorBuilder(in, converter);

    }

    private static MediaConverter getConverter(String contentType) throws IOException {

        if (!MEDIA_CONVERTERS.containsKey(contentType)) {
            throw new IOException("Thumbnailer does not currently support "
                    + "file type '" + contentType + "'.  Unable to create thumbnail.");
        }
        return MEDIA_CONVERTERS.get(contentType);
    }

    private static String getContentType(InputStream is, String fileName)
            throws IOException {
        TikaConfig config = TikaConfig.getDefaultConfig();
        Detector detector = config.getDetector();

        TikaInputStream stream = TikaInputStream.get(is);

        Metadata metadata = new Metadata();
        if (fileName != null) {
            metadata.add(Metadata.RESOURCE_NAME_KEY, fileName);
        }
        return detector.detect(stream, metadata).toString();
    }

    /**
     * builder implementation using the Thumbnailator library
     */
    private static class ThumbnailatorBuilder extends ConversionBuilder {

        public ThumbnailatorBuilder(InputStream is, MediaConverter converter) {
            super(is, converter);
        }

        @Override
        public void save(BufferedImage img, OutputStream out) throws IOException {
            Thumbnails.Builder builder = Thumbnails.of(img)
                    .outputQuality(quality)
                    .outputFormat(outputFormat);
            if (width != null) {
                builder.width(width)
                        .keepAspectRatio(keepAspectRatio);
            }
            if (height != null) {
                builder.height(height)
                        .keepAspectRatio(keepAspectRatio);
            }
            if (watermark != null) {
                builder.watermark(watermark, opacity);
            }
            builder.toOutputStream(out);
        }

    }

    private static abstract class ConversionBuilder implements Builder {

        private final InputStream is;
        private final MediaConverter converter;
        protected String outputFormat = DEFAULT_OUTPUT_FORMAT;
        protected boolean keepAspectRatio = true;
        protected double quality = 1.0;
        protected Integer width;
        protected Integer height;
        protected BufferedImage watermark;
        protected Float opacity;

        public ConversionBuilder(InputStream is, MediaConverter converter) {
            this.is = is;
            this.converter = converter;
        }

        @Override
        public Builder watermark(BufferedImage image, float opacity) {
            this.watermark = image;
            this.opacity = opacity;
            return this;
        }

        @Override
        public Builder width(int width) {
            this.width = width;
            return this;
        }

        @Override
        public Builder height(int height) {
            this.height = height;
            return this;
        }

        @Override
        public Builder quality(double quality) {
            this.quality = quality;
            return this;
        }

        @Override
        public Builder keepAspectRatio(boolean aspect) {
            this.keepAspectRatio = aspect;
            return this;
        }

        @Override
        public Builder format(String format) {
            if (ACCEPTABLE_IMG_FORMATS.contains(format)) {
                outputFormat = format;
            }
            return this;
        }

        @Override
        public final void create(OutputStream out) throws IOException {
            save(converter.convert(is), out);
        }

        @Override
        public final void create(File outputFile) throws IOException {
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            try (OutputStream out = new BufferedOutputStream(
                    new FileOutputStream(outputFile))) {
                create(out);
            }
        }

        abstract protected void save(BufferedImage img, OutputStream out)
                throws IOException;

    }

    /**
     * Fluid interface to create a thumbnail - concept borrowed from
     * {@link net.coobird.thumbnailotor.Thumbnails.Builder}, but simplified for
     * our needs. This interface also prevents coupling applications to a
     * implementation libraries.
     *
     */
    public interface Builder {

        /**
         * Indicates if the aspect ratio of the thumbnail should be kept, the
         * default is true.
         *
         * @param aspect
         * @return
         */
        Builder keepAspectRatio(boolean aspect);

        /**
         * Sets the quality of the compression algorithm
         *
         * @param quality
         * @return
         */
        Builder quality(double quality);

        /**
         * Set the height of the thumbnail
         *
         * @param height
         * @return
         */
        Builder height(int height);

        /**
         * Sets the width of the thumbnail
         *
         * @param width
         * @return
         */
        Builder width(int width);

        /**
         * Sets the thumbnail output format.
         *
         * Valid format names can be obtained by calling the
         * ImageIO.getWriterFormatNames() method.
         *
         * @param format
         * @return
         */
        Builder format(String format);

        /**
         * Adds a watermark to the thumbnail.
         *
         * @param image
         * @param opacity
         * @return
         */
        Builder watermark(BufferedImage image, float opacity);

        /**
         * Creates the thumbnail, saving it to the File
         *
         * @param outputFile
         * @throws java.io.IOException
         */
        void create(File outputFile) throws IOException;

        /**
         * Creates the thumbnail, writing to the OutputStream
         *
         * @param out
         * @throws java.io.IOException
         */
        void create(OutputStream out) throws IOException;

    }
}
