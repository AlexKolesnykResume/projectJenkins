package com.project.tests.utilities;

import java.io.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFolder {
    private static final String reportName1 = "AutomationReport1.zip";
    private static final String reportName2 = "AutomationReport2.zip";

    public static void zipExecutionReport() {
        String timeStamp = "[" + ZonedDateTime.now(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("HH.mm.ss")) + "]";
        try {
            zipFolder(new File(ConfigProvider.getAsString("reportSourcePath1")), new File(reportName1));
//            zipFolder(new File(ConfigProvider.getAsString("reportSourcePath2")), new File(reportName2));
        } catch (IOException e) {
            System.out.println(timeStamp + "Exception in ZipFolder class: " + e.getMessage());
        }
    }

    private static void zipFolder(File srcFolder, File destZipFile) throws IOException {
        FileOutputStream fileWriter = new FileOutputStream(destZipFile);
        ZipOutputStream zip = new ZipOutputStream(fileWriter);
        addFolderToZip(srcFolder, srcFolder, zip);
    }

    private static void addFileToZip(File rootPath, File srcFile, ZipOutputStream zip) throws IOException {
        if (srcFile.isDirectory()) {
            addFolderToZip(rootPath, srcFile, zip);
        } else {
            byte[] buf = new byte[1024];
            int length;
            try (FileInputStream inputStream = new FileInputStream(srcFile)) {
                String name = srcFile.getPath();
                name = name.replace(rootPath.getPath(), "");
                System.out.println("Zip " + srcFile + "\n to " + name);
                zip.putNextEntry(new ZipEntry(name));
                while ((length = inputStream.read(buf)) > 0) {
                    zip.write(buf, 0, length);
                }
            }
        }
    }

    private static void addFolderToZip(File rootPath, File srcFolder, ZipOutputStream zip) throws IOException {
        for (File fileName : Objects.requireNonNull(srcFolder.listFiles())) {
            addFileToZip(rootPath, fileName, zip);
        }
    }
}
