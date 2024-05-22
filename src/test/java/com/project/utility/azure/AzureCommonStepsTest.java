package com.project.utility.azure;

import com.project.pages.CommonSteps;
import com.project.tests.utilities.ConfigProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AzureCommonStepsTest extends CommonSteps {
    private static final Logger LOGGER = LogManager.getLogger(AzureCommonStepsTest.class);

    String accountName = null;
    String shareName = null;
    String filePath = null;
    String sasToken = null;
    static String url = null;
    static HttpURLConnection connection = null;

    public AzureCommonStepsTest() {
        this.accountName = "storageopastg";
        this.shareName = getShareName();
//        this.sasToken = decodeBase64(VaultApi.getVaultValueForKey(VaultKeys.FILE_SHARE_SAS_TOKEN_BODY));
    }

    public void concatUrl() {
        this.filePath = getRunTimeProperty("filePath");
        url = String.format("https://%s.file.core.windows.net/%s%s%s", accountName, shareName, filePath, sasToken);
    }

    private String getShareName() {
        String environment = ConfigProvider.getAsString("Environment");
        String shareName = null;
        switch (environment) {
            case "CloudStage":
                shareName = "stg";
                break;
            default:
                Assertions.fail("Environment is lot listed on AzureCommonSteps class.");
        }
        return shareName;
    }

    public static List<String> getFileContentFromAzure(String fileName) {
        createHttpURLConnection();
        return extractZipContents(bufferedInputStream(), fileName);
    }

    private static List<String> extractZipContents(byte[] fileData, String expectedFileName) {
        // Extract the contents of th zip file.
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(fileData));
        ZipEntry zipEntry = null;
        List<String> fileContents = null;
        try {
            zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                if (fileName.contains(expectedFileName)) {
                    ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
                    byte[] fileBuffer = new byte[1024];
                    int fileByteRead = -1;
                    while ((fileByteRead = zipInputStream.read(fileBuffer)) != -1) {
                        fileOut.write(fileBuffer, 0, fileByteRead);
                    }
                    fileOut.close();
                    fileContents = readLinesFromByteArray(fileOut.toByteArray(), "UTF-8");
                    LOGGER.info("File [" + fileName + "] has the content [" + fileContents + "]");
                    break;
                }
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.closeEntry();
            zipInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return fileContents;
    }

    private static byte[] bufferedInputStream() {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(connection.getInputStream());
            outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int byteRead = -1;
            while ((byteRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, byteRead);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return outputStream.toByteArray();
    }

    // Create HttpURLConnection object to make the API call.
    private static void createHttpURLConnection() {
        try {
            connection = (HttpURLConnection) new URI(url).toURL().openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() != 200) {
                Assertions.fail("Failing to create HttpURLConnection. Response status code is [" + connection.getResponseCode() + "]");
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    //Method to read the lines from a byte array
    public static List<String> readLinesFromByteArray(byte[] bytes, String charsetName) {
        List<String> lines = new ArrayList<>();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charsetName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return lines;
    }

    public List<String> getFilesNames() {
        createHttpURLConnection();
        return getFilesNames(getXmlFromConnectionInputStream());
    }

    private String getXmlFromConnectionInputStream() {
        StringBuilder responseBuilder = null;
        try {
            // Read the response from the API call.
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Name")) {
                    responseBuilder.append(line);
                }
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return String.valueOf(responseBuilder).replaceAll("\uFEFF", "");
    }

    private List<String> getFilesNames(String xml) {
        List<String> names = new ArrayList<>();
        String[] filesArray = xml.split("</File>");
        Pattern pattern = Pattern.compile("<Name>(.+\\.zip)</Name>");
        for (String file : filesArray) {
            Matcher matcher = pattern.matcher(file);
            if (matcher.find()) {
                names.add(matcher.group());
            }
        }
        return names;
    }
}
