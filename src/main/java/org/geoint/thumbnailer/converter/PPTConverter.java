package org.geoint.thumbnailer.converter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;

/**
 * Converts a Microsoft Powerpoint to an image using the first slide in the
 * presentation.
 */
public class PPTConverter implements MediaConverter {

    private final static String[] MEDIA_TYPES = {"application/vnd.ms-powerpoint"};

    @Override
    public String[] supportedMediaTypes() {
        return MEDIA_TYPES;
    }

    @Override
    public BufferedImage convert(InputStream in) throws IOException {
        SlideShow ppt = new SlideShow(in);
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
        return img;
    }

}
