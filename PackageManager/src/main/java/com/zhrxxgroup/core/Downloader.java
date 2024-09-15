package com.zhrxxgroup.core;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Downloader {

    public static void unzip(String zipFilePath, String destDir) throws IOException {
        File dir = new File(destDir);
        if (!dir.exists()) dir.mkdirs();

        try (FileInputStream fis = new FileInputStream(zipFilePath);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                String filePath = destDir + File.separator + zipEntry.getName();
                if (!zipEntry.isDirectory()) {
                    extractFile(zis, filePath);
                } else {
                    File dirEntry = new File(filePath);
                    dirEntry.mkdirs();
                }
                zis.closeEntry();
                zipEntry = zis.getNextEntry();
            }
        }

        File zipfile = new File(zipFilePath);
        if (zipfile.delete()) {
            System.out.println(config.prefix + "Deleted zip file: " + zipFilePath);
        } else {
            System.out.println(config.prefix + "Failed to delete zip file: " + zipFilePath);
        }
    }

    private static void extractFile(ZipInputStream zis, String filePath) throws IOException {
        File file = new File(filePath);
        // Ensure parent directories exist
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zis.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }

    public boolean download(String packageName, double version, String author) throws IOException {
        String fileUrl = config.URL + "/" + author + "-" + packageName + "-" + version + ".zip";
        URL url = new URL(fileUrl);

        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        // httpConn.setRequestMethod("POST");
        int responseCode = httpConn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10, disposition.length() - 1);
                }
            } else {
                fileName = url.getPath().substring(url.getPath().lastIndexOf("/") + 1);
            }

            String userHome = System.getProperty("user.home");
            String saveDir = userHome + File.separator + config.PMN + File.separator + "packages";

            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String saveFilePath = saveDir + File.separator + fileName;
            long totalBytesRead = 0;
            int percentCompleted = 0;

            try (InputStream inputStream = httpConn.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(saveFilePath)) {

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    if (contentLength > 0) {
                        totalBytesRead += bytesRead;
                        percentCompleted = (int) (totalBytesRead * 100 / contentLength);
                        String progressBar = "[" + "=".repeat(percentCompleted / 2) + " ".repeat(50 - percentCompleted / 2) + "]";
                        System.out.print("\rProgress: " + progressBar + " " + percentCompleted + "%");
                    }
                }

                System.out.println("\nDownload completed.");
            }

            String zipFilePath = saveFilePath;
            String destDir = userHome + File.separator + config.PMN + File.separator + "packages" + File.separator + packageName + "-" + version;

            unzip(zipFilePath, destDir);

            return true;
        } else {
            System.out.println(config.prefix + "No file to download. Server replied with response code: " + responseCode);
            return false;
        }
    }
}
