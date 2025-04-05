package com.qrcode.matcher;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import jcifs.config.PropertyConfiguration;
import jcifs.context.BaseContext;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

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

    public static void uploadFile(String localFilePath, String smbUrl, String username, String password) {
//        String localFilePath = "/storage/emulated/0/Download/sample.txt"; // Android file path
//        String smbUrl = "smb://192.168.1.100/shared_folder/sample.txt"; // Target PC path
//        String username = "your_username";
//        String password = "your_password";
        try {
            // Setup SMB authentication
            Properties properties = new Properties();
            properties.setProperty("jcifs.smb.client.username", username);
            properties.setProperty("jcifs.smb.client.password", password);

            BaseContext bc = new BaseContext(new PropertyConfiguration(properties));

            // Create SMB file reference
            SmbFile smbFile = new SmbFile(smbUrl, bc);
            SmbFileOutputStream smbOut = new SmbFileOutputStream(smbFile);

            // Read local file
            File localFile = new File(localFilePath);
            FileInputStream fis = new FileInputStream(localFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                smbOut.write(buffer, 0, bytesRead);
            }

            // Close streams
            fis.close();
            smbOut.close();

            System.out.println("File uploaded successfully to: " + smbUrl);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
