# Text-Extraction-Scanned-Pdf
In pdf documents text handling requires quite a lot of preparatory work that may involve handling font encoding,decoding the raw text data streams into more usable data
and for the case of scanned pdf,preprocessing them for reliable text extraction. There is no straight-forward way of extracting text from scanned pdf documents (images embedded on a documet)
rather called non-searchable pdf especially when working with java.

Here I show you how you can extract text from scanned pdf document using Apache Tika Ocr engine and Tesseract OCR  in java

# Apache Tika OCR
This works well for some scanned pdf and fails terribly on others as it requires preprocessed scanned pdfs for better performace.
Preprocessing pdfs such as noise removal,rotation,border removal,re-scaling,or even enhancing text threshold is **No JOKe**. Eve though you can use openCv(javacv)

This prompted me to try out tesseract.

# Tesseract



