package tika.ocr;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExtractTextFromScannedPdf {

    public static void main(String[] args) {
      String filename = "path/to/file/filename.pdf";

        try {
            extractTextFromScannedPdfWithTikaOCR(filename);

        } catch (TikaException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }





    public static void extractTextFromScannedPdfWithTikaOCR(String fileName) throws TikaException, SAXException,IOException {

        InputStream stream = null;
        stream = new FileInputStream(fileName);

        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1);

        Metadata metadata = new Metadata();

        TesseractOCRConfig config = new TesseractOCRConfig();
        PDFParserConfig pdfConfig = new PDFParserConfig();
        pdfConfig.setExtractInlineImages(true);


        ParseContext parseContext = new ParseContext();
        parseContext.set(TesseractOCRConfig.class, config);
        parseContext.set(PDFParserConfig.class, pdfConfig);
        parseContext.set(Parser.class, parser); //need to add this to make sure recursive parsing happens!

        parser.parse(stream, handler, metadata, parseContext);

        // Print extracted text to console or make use of it as appropriate
        System.out.println(handler.toString());

        if (stream != null)
            try {
                stream.close();
            } catch (IOException e) {
                System.out.println("Error closing stream");
            }
    }

}
