package org.geoint.thumbnailer;

import java.awt.Desktop;
import java.io.File;
import java.io.InputStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class ThumbnailerTest {

    @Test
    public void testPNG() throws Exception {
        File outFile = new File(System.getProperty("java.io.tmpdir")
                + File.separator + "outPNG.jpg");
        try (InputStream in = ThumbnailerTest.class.getResourceAsStream("/example_img.png")) {
            Thumbnailer.thumb(in).height(400).create(outFile);
            assertTrue(outFile.exists());
//            Desktop.getDesktop().open(outFile);
        } finally {
            if (outFile.exists()) {
                outFile.delete();
            }
        }
    }

    @Test
    public void testPPT() throws Exception {
        File outFile = new File(System.getProperty("java.io.tmpdir")
                + File.separator + "outPPT.jpg");
        String testFileName = "example_ppt.ppt";
        try (InputStream in = ThumbnailerTest.class.getResourceAsStream("/" + testFileName)) {
            Thumbnailer.thumb(in).height(400).create(outFile);
            assertTrue(outFile.exists());
//            Desktop.getDesktop().open(outFile);
        } finally {
            if (outFile.exists()) {
                outFile.delete();
            }
        }
    }

    @Test
    public void testPPTX() throws Exception {
        File outFile = new File(System.getProperty("java.io.tmpdir")
                + File.separator + "outPPTX.jpg");
        String testFileName = "example_pptx.pptx";
        try (InputStream in = ThumbnailerTest.class.getResourceAsStream("/" + testFileName)) {
            Thumbnailer.thumb(in).height(400).create(outFile);
            assertTrue(outFile.exists());
//            Desktop.getDesktop().open(outFile);
        } finally {
            if (outFile.exists()) {
                outFile.delete();
            }
        }
    }
    
    @Test
    public void testPDF() throws Exception {
        File outFile = new File(System.getProperty("java.io.tmpdir")
                + File.separator + "outPDF.jpg");
        String testFileName = "example_pdf.pdf";
        try (InputStream in = ThumbnailerTest.class.getResourceAsStream("/" + testFileName)) {
            Thumbnailer.thumb(in).height(400).create(outFile);
            assertTrue(outFile.exists());
//            Desktop.getDesktop().open(outFile);
        } finally {
            if (outFile.exists()) {
                outFile.delete();
            }
        }
    }
}
