package org.geoint.thumbnailer.converter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

/**
 *
 */
public class PDFConverter implements MediaConverter {

    private final static String[] MEDIA_TYPES = {"application/pdf"};

    @Override
    public String[] supportedMediaTypes() {
        return MEDIA_TYPES;
    }

    @Override
    public BufferedImage convert(InputStream in) throws IOException {
        PDDocument doc = PDDocument.load(in);
        List<PDPage> pages = doc.getDocumentCatalog().getAllPages();
        if (pages.isEmpty()) {
            throw new IOException("PDF did not contains any pages.");
        }
        return pages.get(0).convertToImage();
    }

}
