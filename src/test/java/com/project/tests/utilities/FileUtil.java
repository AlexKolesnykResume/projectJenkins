package com.project.tests.utilities;

//import com.itextpdf.text.pdf.AcroFields;
//import com.itextpdf.text.pdf.PdfReader;
//import com.itextpdf.text.pdf.XfaForm;
//import org.apache.pdfbox.cos.COSDocument;
//import org.apache.pdfbox.io.RandomAccessRead;
//import org.apache.pdfbox.pdfparser.PDFParser;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Assertions;
import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

public class FileUtil {
    public FileUtil() {

    }

    public void print(String printText) {
        System.out.println(printText);
    }

    //This method is used to return properties from filename.properties file
    public Properties returnProperties(File file) {
        Properties config = new Properties();
        FileInputStream fileInput = null;
        try {
            fileInput = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        //load properties file
        try {
            config.load(fileInput);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return config;
    }

    public File createPropertyFile(String fileName) throws IOException {
        boolean bool = false;
        File file = new File(fileName);
        bool = file.createNewFile();
        return file;
    }

    //This method is used to Delete existing file from the folder(Filepath with file extension to be sent)
    public void deleteFile(String fileLocation, String fileNameWithExt) {
        //String delFileName = "document.pdf";
        try {
            File file = new File(fileLocation + File.separator + fileNameWithExt);
            if (file.exists()) {
                System.out.println("Deleting the Existing File ");
                file.delete();
            }
        } catch (Exception e) {
            Assertions.fail("Exception in 'deleteFile' method");
        }
    }

    // TODO: This Method is used to validate Downloaded PDF file
    // TODO: This PDF Method needs a Re-check
//    public void PDFValidationFromLocal(String fileLocation, String fileNameWithExt) {
//        boolean flag = false;
//        String reqTextInPDF = "reference number";
//        PDFTextStripper pdfStripper = null;
//        PDDocument pdDoc = null;
//        COSDocument cosDoc = null;
//        String parsedText = null;
//
//        String pdfFileName = "document.pdf";
//        try {
////            path = System.getProperties("user.home") + "\\Downloads\\" + pdfFileName;
//            File file1 = new File(fileLocation + File.separator + fileNameWithExt);
////            URL url = new URL(strURL);
//            PDFParser parser = new PDFParser((RandomAccessRead) new RandomAccessFile(file1, "r"));
//
//            parser.parse();
//            cosDoc = parser.getDocument();
//            pdfStripper = new PDFTextStripper();
//            pdfStripper.setStartPage(1);
//            pdfStripper.setEndPage(2);
//
//            pdDoc = new PDDocument(cosDoc);
//            parsedText = pdfStripper.getText(pdDoc);
//        } catch (MalformedURLException e2) {
//            System.err.println("URL string could not be parsed " + e2.getMessage());
//        } catch (IOException e) {
//            System.err.println("Unable to open PDF Parser. " + e.getMessage());
//            try {
//                if (cosDoc != null) {
//                    cosDoc.close();
//                }
//                if (pdDoc != null) {
//                    pdDoc.close();
//                }
//            } catch (Exception e1) {
//                Assert.fail("Exception in 'PDFValidationFromLocal' method");
//            }
//        }
//        System.out.println("++++++++++++");
//        System.out.println(parsedText);
//        System.out.println("++++++++++++");
//    }

    // This Method is used to upload a file in the browse option from UI using Robot keys
    public void fileUpload(String fileLocation, String fileNameWithExt) {
        try {
            StringSelection stringSelection = new StringSelection(fileLocation + File.separator + fileNameWithExt);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, stringSelection);
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            Thread.sleep(1000);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } catch (Exception e) {
            Assertions.fail("Exception is 'fileUpload' method");
        }
    }

    //This method is used to Read the text file line for particular value match
    public String readTextFileByLine(String fileLocation, String fileNameWithExt, String value) {
        FileReader fr;
        BufferedReader txtReader = null;
        String text = null;
        try {
            System.out.println("Entered Reading a file mode");
            String currentLine;
            text = null;
            fr = new FileReader(fileLocation + File.separator + fileNameWithExt);
            txtReader = new BufferedReader(fr);
            while ((currentLine = txtReader.readLine()) != null) {
                if (currentLine.contains(value)) {
                    System.out.println(currentLine);
                    text = currentLine;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (txtReader != null) {
                    txtReader.close();
                }
            } catch (IOException e) {
                System.out.println("Exception in 'readTextFileByLine' method");
                System.out.println(e.getMessage());
                text = null;
            }
        }
        return text;
    }

    //This method will return the String between the two position numbers passed
    public void textFileStringPosition(String textLine, String value, int numStart, int numEnd) {
        String positionedStringValue = textLine.substring(numStart - 1, numEnd - 1);
        print(positionedStringValue);
        if (positionedStringValue.contentEquals(value)) {
            print("The value is matched");
        } else {
            Assertions.fail("Element Match Found Exception in 'textFileStringPosition' method: ");
        }
    }

    //This method will return the text from the PDF
//    public String pdfTextReader(String fileLocation, String fileNameWithExt) throws IOException {
//        String text;
//        try {
//            File file = new File(fileLocation + File.separator + fileNameWithExt);
//            PDDocument document = PDDocument.load(file);
//            PDFTextStripper pdfStripper = new PDFTextStripper();
//            text = pdfStripper.getText(document);
//            print(text);
//        } catch (Exception e) {
//            print("Exception found in 'pdfTextReader' method: " + e);
//            text = null;
//        }
//        return text;
//    }

    //This method is used to extract the XML format based Generated PDF and stores in XML.
    //xml validation part needs to be linked to the code

//    public void extractXmlPDF(String fileLocation, String fileNameWithExt, String destination, String pdfXmlName) {
//        try {
//            PdfReader reader = new PdfReader(fileLocation + File.separator + fileNameWithExt);
//            AcroFields form = reader.getAcroFields();
//            XfaForm xFa = form.getXfa();
//            Node node = xFa.getDatasetsNode();
//            Transformer tf = TransformerFactory.newInstance().newTransformer();
//            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//            tf.setOutputProperty(OutputKeys.INDENT, "yes");
//            FileOutputStream os = new FileOutputStream(destination + File.separator + pdfXmlName + ".pdf");
//            tf.transform(new DOMSource(node), new StreamResult(os));
//            reader.close();
//        } catch (Exception e) {
//            Assert.fail("Exception occurred in 'extractXmlPDF' method: " + e);
//        }
//    }

    public String unZipGzFile(String fileNameWithExt, String zipFileLocation) {
        byte[] buffer = new byte[1024];
        String fileName = null;
        String extractedFileNameWithExt = null;
        try {
            if (fileNameWithExt.contains(".gz")) {
                String[] fileNm = fileNameWithExt.split("\\.");
                fileName = fileNm[0];
                extractedFileNameWithExt = fileNm[0] + fileNm[1];
                print("Entered location of the Zip file");
                GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(zipFileLocation + File.separator + fileNameWithExt));
                print("Zip file is getting extracted and placed in the same file location");
                if (fileNm[1].contains("xml")) {
                    FileOutputStream out = new FileOutputStream(zipFileLocation + File.separator + fileName + ".xml");
                    int len;
                    while ((len = gzis.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    gzis.close();
                    out.close();
                    print("Extracted: " + fileNameWithExt);
                } else if (fileNm[1].contains("txt")) {
                    FileOutputStream out = new FileOutputStream(zipFileLocation + File.separator + fileName + ".txt");
                    int len;
                    while ((len = gzis.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    gzis.close();
                    out.close();
                    print("Extracted: " + fileNameWithExt);
                } else {
                    FileOutputStream out = new FileOutputStream(zipFileLocation + File.separator + fileName + ".txt");
                    int len;
                    while ((len = gzis.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    gzis.close();
                    out.close();
                    print("Extracted: " + fileNameWithExt);
                }
            } else {
                print("The obtained file is not a zip file");
                print("File is saved in the location");
            }
        } catch (Exception e) {
            Assertions.fail("Exception in 'unZipGzFile' method: " + e);
        }
        return extractedFileNameWithExt;
    }
}
