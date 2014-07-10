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
    public void testResizeUsingJavaAlgo() throws Exception {
        File outFile = new File(System.getProperty("java.io.tmpdir")
                + File.separator + "outImg.jpg");
        try (InputStream in = ThumbnailerTest.class.getResourceAsStream("/example_img.png")) {
            Thumbnailer.thumb(in, outFile.getName(), outFile, 600, 400);
            assertTrue(outFile.exists());
//            Desktop.getDesktop().open(outFile);
        } finally {
//            if (outFile.exists()) {
//                outFile.delete();
//            }
        }
    }

    @Test
    public void testCreatePowerpointThumb() throws Exception {
        File outFile = new File(System.getProperty("java.io.tmpdir")
                + File.separator + "outImg.jpg");
        String testFileName = "example_ppt.ppt";
        try (InputStream in = ThumbnailerTest.class.getResourceAsStream("/" + testFileName)) {
            Thumbnailer.thumb(in, testFileName, outFile, 600, 400);
            assertTrue(outFile.exists());
//            Desktop.getDesktop().open(outFile);
        } finally {
            if (outFile.exists()) {
                outFile.delete();
            }
        }
    }
// @Test
    public void testCreatePptxThumb() throws Exception {
        File outFile = new File(System.getProperty("java.io.tmpdir")
                + File.separator + "outImg.jpg");
        String testFileName = "example_pptx.pptx";
        try (InputStream in = ThumbnailerTest.class.getResourceAsStream("/" + testFileName)) {
            Thumbnailer.thumb(in, testFileName, outFile, 600, 400);
            assertTrue(outFile.exists());
//            Desktop.getDesktop().open(outFile);
        } finally {
            if (outFile.exists()) {
                outFile.delete();
            }
        }
    }
}
