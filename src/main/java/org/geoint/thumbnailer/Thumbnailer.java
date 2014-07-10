package org.geoint.thumbnailer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.tika.Tika;

/**
 *
 */
public class Thumbnailer {

    public static boolean thumb(InputStream src, String origFileName,
            File dest, int width, int height) throws IOException {

        final String contentType = getContentType(src, origFileName);
        switch (contentType) {
            case "image/jpeg":
            //intentional fall through
            case "image/png":
            //intentional fall through
            case "image/x-png":
            //intentional fall through
            case "image/gif":
                return resize(src, dest, width, height);
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
                //powerpoint 2007+
                try (ByteArrayOutputStream imgOut = new ByteArrayOutputStream()) {
                    pptxToImg(src, imgOut);
                    try (InputStream imgIn = new ByteArrayInputStream(imgOut.toByteArray())) {
                        return resize(imgIn, dest, width, height);
                    }
                }
            case "application/vnd.ms-powerpoint":
                try (ByteArrayOutputStream imgOut = new ByteArrayOutputStream()) {
                    pptToImg(src, imgOut);
                    try (InputStream imgIn = new ByteArrayInputStream(imgOut.toByteArray())) {
                        return resize(imgIn, dest, width, height);
                    }
                }
            case "application/pdf":
            //intentional fall through
            case "application/vnd.google-earth.kml+xml":
            //intentional fall through
            case "application/vnd.google-earth.kmz":
            //intentional fall through
            default:
                throw new IOException("Thumbnailer does not currently support "
                        + "file type '" + contentType + "'.  Unable to create thumbnail from "
                        + origFileName);
        }
    }

    /**
     * Returns the content type or application/octet-stream if it couldn't be
     * determined
     *
     * @param is
     * @param fileName
     * @return
     * @throws IOException
     */
    private static String getContentType(InputStream is, String fileName)
            throws IOException {
        //currently we only check the file name...we'll change this to a 
        //composite detector if it's determined we need to
        return new Tika().detect(fileName);
    }

    private static boolean resize(InputStream src, File dest, int width, int height) throws IOException {
        BufferedImage sourceImage = ImageIO.read(src);
        double ratio = (double) sourceImage.getWidth() / sourceImage.getHeight();
        if (width < 1) {
            width = (int) (height * ratio + 0.4);
        } else if (height < 1) {
            height = (int) (width / ratio + 0.4);
        }

        Image scaled = sourceImage.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
        BufferedImage bufferedScaled = new BufferedImage(scaled.getWidth(null), scaled.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedScaled.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(scaled, 0, 0, width, height, null);
        dest.createNewFile();
        writeJpeg(bufferedScaled, dest.getCanonicalPath(), 1.0f);
        return true;
    }

    /**
     * Write a JPEG file setting the compression quality.
     *
     * @param image a BufferedImage to be saved
     * @param destFile destination file (absolute or relative path)
     * @param quality a float between 0 and 1, where 1 means uncompressed.
     * @throws IOException in case of problems writing the file
     */
    private static void writeJpeg(BufferedImage image, String destFile, float quality)
            throws IOException {
        ImageWriter writer = null;
        FileImageOutputStream output = null;
        try {
            writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
            output = new FileImageOutputStream(new File(destFile));
            writer.setOutput(output);
            IIOImage iioImage = new IIOImage(image, null, null);
            writer.write(null, iioImage, param);
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (writer != null) {
                writer.dispose();
            }
            if (output != null) {
                output.close();
            }
        }
    }

    private static void pptToImg(InputStream src, OutputStream imgOut)
            throws IOException {
        SlideShow ppt = new SlideShow(src);

        Dimension pgsize = ppt.getPageSize();

        Slide[] slide = ppt.getSlides();
        if (slide.length < 1) {
            throw new IOException("Cannot convert PPT to image, no slides found.");
        }
        BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, 1);

        Graphics2D graphics = img.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        graphics.setColor(Color.white);
        graphics.clearRect(0, 0, pgsize.width, pgsize.height);
        graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

        slide[0].draw(graphics);
        System.out.println("title: " + slide[0].getTitle());
        ImageIO.write(img, "png", imgOut);
    }
    
    private static void pptxToImg(InputStream src, OutputStream imgOut)
            throws IOException {
        SlideShow ppt = new SlideShow(src);

        Dimension pgsize = ppt.getPageSize();

        Slide[] slide = ppt.getSlides();
        if (slide.length < 1) {
            throw new IOException("Cannot convert PPT to image, no slides found.");
        }
        BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, 1);

        Graphics2D graphics = img.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        graphics.setColor(Color.white);
        graphics.clearRect(0, 0, pgsize.width, pgsize.height);
        graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

        slide[0].draw(graphics);
        System.out.println("title: " + slide[0].getTitle());
        ImageIO.write(img, "png", imgOut);
    }
}
