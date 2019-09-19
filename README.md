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

## Example
Input pdf https://github.com/fraponyo94/Text-Extraction-Scanned-Pdf/blob/master/sample-scanned-pdfs/PublicWaterMassMailing.pdf

Extracted Text
  ```R RRRRRRRReSeeeRRRAAAhA A A A A
<S> £
& 2\ (e
Missouri Department of Health and Senior Services ( "’ )
(0] 5 FEPB P.0. Box 570, Jefferson City, MO 65102-0570  Phone: 573-751-6400  FAX: 573-751-6010 S
R é‘g RELAY MISSOURI for Hearing and Speech Impaired 1-800-735-2966_VOICE 1-800-735-2466 M
& Peter Lyskowski Jeremiah W. (Jay) Ni
Rz Lo o B e
Missouri Public Water Systems
November 10, 2015
Dear Public Water System Owners/Operators:
The Missouri State Public Health Laboratory (MSPHL) is in the process of implementing a new
Laboratory Information Management System (LIMS) in its drinking water bacteriology testing
laboratory. The OpenELIS (OE) LIMS will provide the laboratory with improved sample management
capability, improved data integrity and reduced potential for human data entry error. In addition, the
system will provide improved reporting capabilities, including direct electronic data exchange with the
Missouri Department of Natural Resources’ (MDNR) Safe Drinking Water Information System
(SDWIS). SDWIS is the computer system MDNR uses to store regulatory water testing data and report
testing results to you and the U.S. Environmental Protection Agency. In addition, the new OE LIMS will
provide a web portal that MSPHL clients can use to access their own test results in real time.
As the MSPHL implements this new computer system, several changes will be made in the way you
collect and submit water samples for testing. This letter and information packet will provide you with
information to help educate you on these changes.
NEW SAMPLE BOTTLES:
Beginning in August 2015, the MSPHL began using a larger sample bottle for water bacterial testing.
This bottle has a shrink wrap seal and two lines to indicate the proper sample volume. Please read the
attached “SAMPLE COLLECTION INSTRUCTIONS?” for details on how to use these new bottles.
Sample volume MUST be within the two lines on the bottle (100 — 120 mL) to be acceptable for
testing. You may continue to use your old bottles until the MSPHL can ship you new ones. Once you
have received the new bottles, please discard or recycle the old bottles.
NEW SAMPLE INFORMATION FORMS:
The traditional sample information “card™ that has been used for more than twenty years is being
replaced by the Environmental Sample Collection Form. An example form is attached. Please read the
attached instructions for information on properly completing the new form.
Changes to the form include the following:
1. Form size is expanded to a single 8 4" x 117 sheet of paper. The form is no longer in a triplicate
carbon copy format. You may choose to photocopy for your records if you prefer. Note: MDNR
does not require a public water system to retain copies of sample collection forms; however, you
might utilize them for system inspections.
2. The form is printed by the OE LIMS and will be pre-populated with your Public Water Supply
ID number, PWS name, address and county. Forms should not be shared with other supplies.
www.health.mo.gov
Healthy Missourians for life.
The Missouri Department of Health and Senior Services will be the leader in promoting, protecting and partnering for health,
AN EQUAL OPPORTUNITY / AFFIRMATIVE ACTION EMPLOYER: Services provided on a nondiscriminatory basis.
sR
Contract operators will be provided with forms for all the supplies they operate. Blank forms will
be available for MDNR Regional Office staff use.
3. The form requires all requested information to be printed by the collector. There are no longer
check boxes for Sample Type or Repeat Location.
4. Facility ID, Sample Collection Point ID and Location for the sampling site MUST be
provided by the collector. This information is available from your MDNR approved PWS
sampling plan. MDNR will be providing all public water systems with a current copy of their
approved sampling plan. This information is required by SDWIS and is used by MDNR to
ensure regulatory compliance requirements have been met. Failure to complete this information
on the sample collection form may result in a non-compliance report from MDNR.
5. A Collector Signature line has been added. The sample collector must sign the form to attest the
information provided is accurate to the best of their knowledge.
The MSPHL will begin shipping the new forms to public water systems in late November or early
December. Please begin using the new forms December 16, 2015. Discard all the old forms (“cards™)
at that time.
NEW SAMPLE INSTRUCTIONS:
Sample instructions have been revised to include changes to the bottle and sampling form. The
instructions include detailed information on how to collect the sample using the new bottle, how to
complete the new sample collection form, how to best ship samples to the MSPHL using the free
MSPHL courier system, and how to register for the new MSPHL web portal. A copy of these
instructions is attached.
NEW WEB PORTAL FOR RESULTS REPORTS
The OE LIMS provides a web portal that may be used by systems to view and print their test result
reports, check status of samples, download sample information into Excel, and receive automated emails
when samples are received at the laboratory, and when sample results are ready to be viewed. For
information on how to gain access to this portal, please contact Shondra Johnson, LIMS Administrator
at Shondra.Johnson@health.mo.gov or at 573-751-3334.
IMPLEMENTATION DATES:
The MSPHL intends to implement the OpenELIS LIMS on December 1, 2015. There will be a two
week testing period in which laboratory staff will run the new LIMS in conjunction with our current
manual, paper-based system to ensure the OE LIMS is operating properly. You may continue to submit
samples as you currently do, using the old sample information card, throughout this time.
On December 16, 2015, the MSPHL plans to “go-live” with the new OE LIMS. Samples submitted
after that date should be submitted on the new Environmental Sample Collection Form. At that time, the
MSPHL Test Results Web Portal will also be available to those systems that have been granted access.
The MSPHL and MDNR understand that there will be a lot of changes to a system that has been in place
for many years. The MSPHL is excited about the added benefits from this new system, and we ask for
your patience as we implement the OpenELIS LIMS at the Missouri State Public Health Laboratory.
LI
If you have any questions, please contact the MSPHL Environmental Bacteriology Unit at 573-751-
3334. You may also contact your MDNR Regional Office for additional information on sample
collection.
Once again, thank you for your patience and understanding as we implement these changes.
Pttt R Hoamses.
Patrick R. Shannon
Manager, Environmental Bacteriology Unit
Missouri Department of Health and Senior Services
State Public Health Laboratory
101 North Chestnut St.
P.O. Box 570
Jefferson City, MO 65102
Phone: 573-751-3334
Email: Pat.Shannon@health.mo.gov
Web: www.health.mo.gov/LabOrder #: 984 [T REPORT TO: BILL TO:
Pages in Order: 1of 1 65 i 82 i
Containers in Order: 1 ADRIAN MO DEPARTMENT OF NATURAL RESOURCES
16 E 5TH ST 1101 RIVERSIDE DRIVE
ADRIAN, MO 64720 JEFFERSON CITY, MO 65102
Requested Analyses/Tests
PUBLIC DRINKING WATER BACTERIAL ANALYSIS
Total Coliform Bacteria and E. coli (Present/Absent Test)
£ PRINT LEGIBLY. Instructions for completing form are supplied in the Collection Kit. For compliance monitoring questions, contact the
o Missouri Department of Natural Resources-Public Drinking Water Branch at (573) 751-5331 or your regional office. For laboratory test
— results or testing questions, contact the Missouri State Public Health Laboratory at (573) 751-3334.
T 9
z S Complete or correct the following information
O =
EB
c = Collected Date: Collected Time
oo
EO
> PWS Id: MO1010001 Facilty 1d: DS
c Qo
wga
£ Sample Type: Sample Collection Point
@ 1d:
n Location: Collector:
Collector Phone: Sample Category: Bacterial
Repeat Location: Bottle Number:
Free Chlorine: Total Chlorine:
Collector Signature: County: BATES
n
Q
2
&
L]
n
&
2
c
[
»
3
< o
s 5
T g,
= Bs
28 3%
GE ~SO For Laboratory Use Only -- Please do not write below this line
8 25 2
E 8 3 é’% Received By: pH:
® £ 5
o= Qg 3| Evidence of Tampering Yes No Evidence of Cooling: | Yes No
Qo3 ® S
OITE =8~ DatePrinted: 2015-11-06 Temperature ( Celsius ):
=2 8R6%5E "
535252 2 E| bottles Received Thermometer ID:
o290 x8E%
BoZa5Ss
R =
S5°08EF
—_ LACE T ACCESSION
L Cm
SD 062015AR AR
L\ (P | SAMPLE COLLECTION INSTRUCTIONS | |O &= mssours
hiid] " 1) | DEPARTMENT OF
w Nel PUBLIC DRINKING WATER for COLIFORM BACTERIA ANALYSIS L\_‘i”j NATURALRESOUREES
This sample kit and collection method is for public drinking water regulatory compliance and special samples.
Only samples collected in bottles supplied by the Missouri State Public Health Laboratory (MSPHL) and collected
in accordance with these instructions will be accepted for testing. PLEASE READ THESE INSTRUCTIONS
COMPLETELY BEFORE COLLECTING SAMPLES.
Sample Containers:
Sample bottles from the MSPHL contain a chlorine neutralizer that is present in powder or liquid form. The bottles
are sterile and ready for use when shipped. Do not rinse the contents from the container and keep the bottle
closed until it is to be filled.
[ i
Shrink Wrap Seal: et
Remove the seal by pulling down on the red strip L e
and pealing shrink wrap from both the cap and ‘,:'.-'
bottle. Discard all shrink wrap. Do not attempt to . 2
reseal lid with shrink wrap still attached. ‘h; 37Ty
", g B Toes
B S ae  oill ine
Two Fill Lines: < P & b
€~ Po < Vin. fill line
Fill the bottle until the water sample level is b R o ?
BETWEEN THE TWO LINES. Place the bottle b S
on a level surface to check the sample level. | 77 e : \
Samples below the 100 mL (lower) line WILL P w> .
NOT BE TESTED due to insufficient sample SRR S A
volume. Samples above the 120 mL (upper) line Vi A e AR y
WILL NOT BE TESTED due to overfilled S e
bottle. Technical protocol and EPA requirements T R8s o
dictate that bottles must have sufficient air space
to add testing reagents and to mix the sample
properly.
If the bottle is overfilled past the 120 mL line, For.More.Information; pleaseiconitat
pour off water until the sample volume is between s ST  Heal Seil .o
the two lines before shipping to MSPHL. MSPHL 2/:11:20;&{?:?{3;‘11:?}1:)bior:;rth and SEniE Services
WILL NOT adjust sample volume once the Er;vironmcmal Bac\c:iology l?‘lnil
samplelisreceivediat the lab, 101 North Chestnut St., P.O. Box 570
Jefferson City, MO 65102
NoPaper Labels Phone: 573-751-3334
There is no longer a label to record sample FAX: 573-522-4032
information on the bottle. DO NOT WRITE ON Fmai]‘ laﬂw*cla?@l]egllll i
I'HE BOTTLE. Please complete a sample
information form for each sample submitted for 3
L . o~ : - J : yww.health.mo.g g
testing. DATE AND TIME OF SAMPLE Website: - wwwhealthmo.qou Lab
COLLECTION and the BOTTLE NUMBER
(from sticker on bottle) ARE REQUIRED. A form
for each bottle is included in this sample kit.
Page 10f 4 LAB 34 Public Water (R10-2015)
LR
Bacteriological Sample Collection Procedures
Assemble all of the sampling supplies. Before you begin, wash your hands
thoroughly before handling supplies. Go to the sampling location(s) specified "i»
in your Missouri Department of Natural Resources (MDNR) approved (r
N sampling site plan. The sample should be taken from a clean, smooth-nosed cold 4
o water faucet if possible. Avoid drinking fountains, leaky faucets, hot/cold
\ mixing faucets and frost-proof yard hydrants since it is not practical to &
iy sterilize these fixtures. If possible, remove any aerators, strainers or hoses ,)‘
@ that are present because they may harbor bacteria. Follow the procedures below s
when collecting the sample. Instructions for completing the environmental B
sampling form are on the following page. =®
C% 1. Open the cold water tap for about 3 minutes before collecting the sample.
This should adequately flush the water line of any debris. Fill sample borle until water level is
L 2. Flame-sterilize the tap and/or chemically disinfect the tap. Do not flame- Detwweer e two lires o tho botte
A sterilize if tap is plastic or if acrators are attached. Disinfect tap by
% thoroughly rinsing both the inside and outside of the tap with a mixture of 50%
house-hold bleach (NaOCl) and 50% tap water. Take extreme care with strong #li'
@ bleach (oxidizing) solutions. s
3. Flush the tap for an additional 3 minutes with cold water, and then reduce i
to a gentle flow to about the width of a pencil. Do not change the water flow X jﬁ
once you have started sampling as this could dislodge contaminants in the tap. \/" F
4. Remove the plastic shrink wrap seal by pulling down on the red strip and 0}
pealing the shrink wrap from both the cap and bottle. Discard the shrink
A wrap. Do not attempt to reseal the lid with shrink wrap still attached.
4 5. Grasp cap along top edge and remove carefully. Do not touch the inside
i with your fingers. Hold the bottle in one hand and the cap in the other. Do not
@ lay the cap down or put it in a pocket! Also, take care not to contaminate the
sterile bottle or cap with your fingers or permit the faucet to touch the inside of
ot s e nd e the bottle. FROST-PROOF HYDRANT
m”‘d‘d""{’;“‘( ey 0. Hold the bottle so that water entering the bottle will not come in contact with
your hands or the outside of the bottle.
Mo i e 2 7. Fill the bottle until the water sample level is BETWEEN THE TWO
Min.Fill Line- LINES on the bottle (100 — 120 ml). Preferably, the sample level should be at T
.: or just slightly above the 100 ml line. Sample levels below the 100 ml (lower) BORORE
K 9’ line WILL NOT BE TESTED due to insufficient sample volume. Sample
- levels above the 120 ml (upper) line WILL NOT BE TESTED due to
® overfilled bottle. If the bottle is overfilled, you may pour off any excess water ~ HoTeoL> e et
to get the sample level between the two lines. Place the cap on the bottle and
screw it down tightly.
8. Fill out the Missouri Department of Health and Senior Services (DHSS)
@; State Public Health Lab (SPHL) Environmental Sample Collection Form
using waterproof ink. See attached document for instructions on properly PONOT 138
completing the sample collection form and for shipping instructions.
9. For single samples, neatly fold the sample collection form into thirds (standard
a i letter fold), roll around the bottle and place in the shipping box. For multiple
%j samples. fold the forms once or more as needed, and place in the shipping box
e @ alongside the samples. If needed, use bubble pack or folded paper to fill space.
” Do not use shredded paper. Seal the box with a single strip of shipping tape and
affix the return address label to the top of the box.
Page20of4 LAB 34 Public Water (R10-2015)
LR
INSTRUCTIONS FOR COMPLETING ENVIRONMENTAL SAMPLE COLLECTION FORM
Public Drinking Water Bacterial Analysis
PRINT LEGIBLY using water proof ink. A standard ink pen is sufficient. Complete ALL sample information lines on the form.
Some sections of the form may already be completed by the laboratory computer system when the forms are printed. To make
corrections, please draw a single line through the inaccurate information and print the corrected information behind it. The
sections of the form and directions for completing each line are as follows:
Order #: For Missouri State Public Health Lab (MSPHL) purposes only. Pages in Order and Containers in Order indicate number of
forms and sample bottles shipped in the sample kit order.
REPORT TO: Public water system’s name and shipping address on file with Missouri Department of Natural Resources (MDNR).
Please review and correct if necessary. Result reports will be mailed to this address.
BILL TO: Section defaulted to the MDNR. There are no charges for public water testing at the MSPHL.
Requested Analysis/Tests:
This section will state PUBLIC DRINKING WATER BACTERIAL ANALYSIS. If it does not, you may have the wrong collection
form. Please contact the MSPHL or MDNR for the proper form. Do not use forms from a local county health agency as those forms are
for private well water samples. Your MDNR Regional Office can provide blank forms for your use.
Complete or correct the following information:
All lines are considered required information. Failure to complete a line may result in an invalid sample.
Collected Date: Enter the date of sample collection in the format YYYY-MM-DD. Use 4 digits for year and 2 digits for month and date.
November 1, 2015 would be written as 2015-11-01.
Collected Time: Enter the time of sample collection using 24-hour military format hh:mm.
PWS ID: If blank, enter your 7-digit Public Water System ID number as assigned by MDNR (MO#######).
Facility ID: Defaulted to DS (Distribution System) for routine samples. If submitting a sample type other than Routine, enter the Facility
ID number from your system’s MDNR approved sample site plan (for example DS#, WL#, WT#).
Sample Type: Enter one of the following options:
Routine — Regular monthly monitoring samples.
Repeat — A series of 3 or 4 repeat samples (4 if you only take 1 routine per month) must be taken for each routine sample that tests
positive (Present) for coliform bacteria. All repeats must be taken on the same day, within 24 hours of being notified of the
coliform positive sample. Site locations are based on the approved site sampling plan. Typically these samples will consist of one
from the site of the original unsafe sample location, one within 5 service connections upstream, one within 5 service connections
downstream, and one from a location specified or approved by MDNR. If your system is a ground water system serving less than
1,000 people without 4 log virus inactivation, one repeat sample (the fourth repeat) may be collected from the source/well prior to
treatment. See Repeat Location below.
Replacement — All samples which are not tested because they were invalid, incomplete information, outdated, broken in transit,
frozen, etc., must be replaced with a single sample from the same location within 24 hours of being notified.
Source/Well — If your system is a ground water system without chlorine contact time (4 log virus inactivation or removal), one
sample must be collected from each well/source, prior to any treatment, active at the time of the positive sample(s).
Special — Any sample that does not count for compliance. These may include samples to check disinfection practices on repairs or
new construction or for seasonal public water systems prior to serving water to the public.
Sample Collection Point ID: Enter the sampling point ID number from your system’s MDNR approved sample site plan. This number is
required. DO NOT LEAVE BLANK. If you have questions about your Sample Collection Point ID, please contact the MDNR.
Location: Enter the address or name of the collection location associated with the Sample Collection Point ID above. (Important Note:
The Location is tied to the Sample Collection Point ID from the approved site sampling plan and will be the location printed on
the final analysis report. If the location entered on the collection form does not match the final report, contact MDNR.)
Collector: Enter your last name, first name.
Collector Phone: Enter your 10-digit day time phone number.
Sample Category: This will always be Bacterial and is already filled out for you.
Repeat Location: If the sample type above is Repeat, enter the repeat location for this sample: upstream, downstream, original, source or
other. If other, please describe the location.
Bottle Number: Enter the number from the label on the bottle. This is used to match collection forms to samples.
Free Chlorine: Enter the free chlorine test level in mg/L (if your system is chlorinated).
Total Chlorine: Enter the total chlorine test level in mg/L (if your system is chlorinated).
Collector Signature: By signing you attest that the information provided is accurate to the best of your knowledge.
County: Enter the county name for the collection point if it is not already filled out for you.
All other sections of the Environmental Sample Collection Form are for MSPHL use only. If you have any questions, please contact the
MSPHL Environmental Bacteriology Unit at (573) 751-3334 or your local MDNR Regional Office (see next page for phone numbers).
Page3of4 LAB 34 Public Water (R10-2015)
e —s
Shipping Instructions
Per U.S. Environmental Protection Agency requirements, public water samples must be received by the laboratory and tested
within 30 hours of the date and time of collection. The MSPHL and MDNR recommend you use the free Department of Health
and Senior Services (DHSS) contract courier for overnight delivery to the MSPHL. This courier picks up at most local public health
agency offices and hospitals (Note: Not all hospitals will accept water samples for courier pick up). For sample drop off locations and
times, please go to http:/www.health.mo.gov/lab/courierservices.php and click on the interactive map or the listing of drop off locations
by county; or you may call the MSPHL courier liaison at (573) 751-4830, or the MDNR Public Drinking Water Branch (PDWB) at (573)
526-1124.
Pleasc note the courier is allowed to pick up samples within one hour of the scheduled time (before or after). The carliest pick up
time is at 10:30 a.m. To ensure your samples meet the transit time requirement of 30 hours, it is important that you collect your samples in
the morning and have them dropped off at the courier pickup point one hour prior to the scheduled time.
Use of the U.S. Postal Service or other commercial carriers such as Fed Ex or UPS will require additional charges and may not meet the
30 hour transit time requirement.
Samples should not be en route to the laboratory over a weekend or state holiday (New Year’s Day, Martin Luther King Day,
Lincoln’s Birthday, Washington’s Birthday, Truman’s Birthday, Memorial Day, Independence Day, Labor Day, Columbus Day. Veteran’s
Day, Thanksgiving Day, and Christmas.)
Public water supplies may use the new MSPHL Test Results Web Portal to retrieve preliminary test results on-line. For information on
how to register as a user for the web portal and to receive email notifications, please contact the MSPHL LIMS Administrator at
shondra.johnson@health.mo.gov or call 573-751-3334. These preliminary test results are for informational purposes only. Official test
results are available on-line within 2 or 3 business days at the MDNR Drinking Water Watch website http:/dnr.mo.gov/DWW/.
In addition, the official bacteriological sample reports will be mailed by MDNR within 4 or 5 business days.
Additional sample bottles can be ordered on-line at http://www.health.mo.gov/lab/specimentestforms.php or by calling the MSPHL
Central Services Unit at (573) 751-4830.
Sometimes in spite of taking all of the precautions you may get a call from MDNR or results by mail notifying you that coliform or
E. coli bacteria are present in your water. You will be given specific instructions that may include collection of repeat samples to confirm
that the first routine sample was not a sampling error. Please call the MDNR Regional Office staff and they will discuss the procedure
with you. See contact information below.
For more information about public water systems, contact the MDNR Public Drinking Water Branch at (573) 751-5331 or your MDNR
Regional Office (counties within each region are listed at http://dnr.mo.gov/regions/index.html) or visit www.dnr.mo.gov/env/wpp/dw-
index.html:
Missouri Department of Natural Resources
Division of Environmental Quality
Water Protection Program
Public Drinking Water Branch
P.0. Box 176
Jefferson City, Missouri 65102
573-751-5331
Kansas City Regional Office Northeast Regional Office Southeast Regional Office
Department of Natural Resources Department of Natural Resources Department of Natural Resources
500 NE Colbern Road 1709 Prospect Drive 2155 North Westwood Blvd.
Lee’s Summit, MO 64086-4710 Macon, MO 63552-1930 Poplar Bluff, MO 63901-1420
(816) 251-0700 (660) 385-6000 (573) 840-9750
Southwest Regional Office St. Louis Regional Office
Department of Natural Resources Department of Natural Resources
2040 West Woodland 7545 South Lindbergh, Suite 210
Springfield, MO 65807-5912 St. Louis, MO 63125
(417) 891-4300 (314) 416-2960
Page 4 of 4 LAB 34 Public Water (R10-2015)
L
  ```






