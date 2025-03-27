package com.qrcode.matcher;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FTPUploader {

    public static boolean uploadFile(String host, String username, String password,
                                     int port, String remoteDirPath,
                                     String localFilePath) {
        FTPClient ftpClient = new FTPClient();
        FileInputStream fileInputStream = null;

        try {
            // Connect to the server
            ftpClient.connect(host, port);

            // Login with credentials
            boolean success = ftpClient.login(username, password);
            if (!success) {
                return false;
            }

            // Set file type to binary
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();

            // Create remote directory if needed
            if (remoteDirPath != null && !remoteDirPath.isEmpty()) {
                ftpClient.makeDirectory(remoteDirPath);
                ftpClient.changeWorkingDirectory(remoteDirPath);
            }

            // Upload file
            File localFile = new File(localFilePath);
            fileInputStream = new FileInputStream(localFile);
            String remoteFileName = localFile.getName();

            boolean uploaded = ftpClient.storeFile(remoteFileName, fileInputStream);
            return uploaded;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
