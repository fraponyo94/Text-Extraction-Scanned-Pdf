# Text-Extraction-Scanned-Pdf
In pdf documents text handling requires quite a lot of preparatory work that may involve handling font encoding,decoding the raw text data streams into more usable data
and for the case of scanned pdf,preprocessing them for reliable text extraction. There is no straight-forward way of extracting text from scanned pdf documents (images embedded on a documet)
rather called non-searchable pdf especially when working with java.

Here I show you how you can extract text from scanned pdf document using Apache Tika Ocr engine and Tesseract OCR  in java

# Apache Tika OCR
This works well for some scanned pdf and fails terribly on others as it requires preprocessed scanned pdfs for better performace.
Preprocessing pdfs such as noise removal,rotation,border removal,re-scaling,or even enhancing text threshold is **No JOKe**. Eve though you can use openCv(javacv).

My example code of Apache Tika does not preprocess the scanned pdf. Works selectively depending on the nature of your scanned/image embedded pdf
  ```   InputStream stream = null;
        stream = new FileInputStream(fileName);

        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1); // 

        Metadata metadata = new Metadata();

        TesseractOCRConfig config = new TesseractOCRConfig();
        PDFParserConfig pdfConfig = new PDFParserConfig();
        pdfConfig.setExtractInlineImages(true);


        ParseContext parseContext = new ParseContext();
        parseContext.set(TesseractOCRConfig.class, config);
        parseContext.set(PDFParserConfig.class, pdfConfig);
        parseContext.set(Parser.class, parser); //for recursive parsing

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
  ```
Checkout **ExtractTextFromScannedPdf.java** class for details

This prompted me to try out tesseract.

# Tesseract
-Use **PdfBox** libary to extract images from the **scannedPdf** 
  ``` LinkedList<BufferedImage> bufferedImages = new LinkedList<>();


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
        
  ```
    I use LinkedList to store BufferedImages for sequential retrieval in FIFO order
    
    
 - Extract text from extracted images using Tesseract Ocr 
  ```
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
  ```

-Append together extracted text from each images of the scanned pdf to have the final result
  ```
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
  ```
  
Check out **TesseractScannedPdfTextExtraction.java class** for details 






