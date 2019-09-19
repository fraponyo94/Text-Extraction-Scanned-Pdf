package tesseract.ocr;

import com.recognition.software.jdeskew.ImageDeskew;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;

public class TesseractScannedPdfTextExtraction {
   static Tesseract tesseract = new Tesseract();


    public static void extractTextFromScannedPdfWithTikaOCR(String fileName) throws IOException {
        //Set path to tesseract tessdata (usually in /usr/share/tessdata after installing Tesseract in linux)
        tesseract.setDatapath("/usr/share/tessdata");

        File file = new File(fileName);

        String ocrResults = extractText(file);
        if (ocrResults == null || ocrResults.equals("")) {
            System.out.println("Empty text");
        }else{
            System.out.println(ocrResults);
        }



    }


    // Preprocess the bufferedImage
    private static BufferedImage correctSkewness(BufferedImage image) {

        /*
         * This method corrects skewness of the image, if necessary
         */

        final double MINIMUM_DESKEW_THRESHOLD = 0.05d;

        ImageDeskew mImage = new ImageDeskew(image);
        double imageSkewAngle = mImage.getSkewAngle(); // determine skew angle
        if ((imageSkewAngle > MINIMUM_DESKEW_THRESHOLD || imageSkewAngle < -(MINIMUM_DESKEW_THRESHOLD))) {
            image = ImageHelper.rotateImage(image, -imageSkewAngle); // deskew image
        }

        return image;
    }



    // Extract text from pdf images
    private static String extractTextFromImage(BufferedImage image) {

        BufferedImage grayImage = ImageHelper.convertImageToGrayscale(image);
        String ocrResults = null;
        try {
            ocrResults = tesseract.doOCR(grayImage).replaceAll("\\n{2,}", "\n");

        } catch (TesseractException e) {
            e.printStackTrace();
        }

        if (ocrResults == null || ocrResults.trim().length() == 0) {

            return null;
        }

        ocrResults = ocrResults.trim();
        // TODO remove the trash that doesn't seem to be words
        return ocrResults;
    }



    /* Check for scanned pdf and return contained images
     * @return LinkedList<BufferedImage>
     * */
    private static LinkedList<BufferedImage> checkScannedPdf(File pdfFile ) throws IOException {
        int images = 0;
        int numberOfPages = 0;

        LinkedList<BufferedImage> bufferedImages = new LinkedList<>();


        PDDocument doc = PDDocument.load(pdfFile);

        PDPageTree list = doc.getPages();

        numberOfPages = doc.getNumberOfPages();

        for (PDPage page : list) {

            PDResources resource = page.getResources();

            for (COSName xObjectName : resource.getXObjectNames()) {

                PDXObject xObject = resource.getXObject(xObjectName);

                if (xObject instanceof PDImageXObject) {
                    PDImageXObject image = (PDImageXObject) xObject;

                    BufferedImage bufferedImage = image.getImage();
                    // Add bufferedImages to list
                    bufferedImages.add(bufferedImage);
                    images++;
                }

            }

        }

        doc.close();

        //  pdf pages if equal to the images === scanned pdf ===
        if (numberOfPages == images) {
            return bufferedImages;
        } else {

            return new LinkedList<>();
        }

    }


    // Extract text
    private static  String extractText(File file) throws IOException {
        StringBuilder extractedText = new StringBuilder("");
        LinkedList<BufferedImage> bufferedImageList = new LinkedList<BufferedImage>();
        bufferedImageList = checkScannedPdf(file);

        if(!bufferedImageList.isEmpty()){
            for(BufferedImage image: bufferedImageList){
                BufferedImage deskewedImage = correctSkewness(image);
                String text = extractTextFromImage(deskewedImage);

                if(text != null ) {
                    extractedText.append(text);
                }
            }
        }

        return extractedText.toString();
    }

}
