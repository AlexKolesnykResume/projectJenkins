package com.project.utility;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.project.pages.CommonSteps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class SSH extends CommonSteps {
    private static final Logger LOGGER = LogManager.getLogger(SSH.class);

    public static String savePrivateKey( String privateKey) {
        String path = "pk.pk";
        File file = new File(path);
        try {
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            if (!privateKey.contains("-----BEGIN RSA PRIVATE KEY-----")) {
                privateKey = "-----BEGIN RSA PRIVATE KEY-----\n" + privateKey + "\n-----END RSA PRIVATE KEY-----";
            }
            bufferedWriter.write(privateKey);
            bufferedWriter.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return path;
    }

    public static @NotNull String execute(String host, String userName, String privateKey, String[] commands) {
        StringBuilder output = new StringBuilder();
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(userName, host, 22);
            jsch.addIdentity(savePrivateKey(privateKey));
            // Skip host-key check
            session.setConfig("StrictHostKeyChecking", "no");
            LOGGER.info("Attempting connection to: [" + host + "]");
            session.connect();
            LOGGER.info("Connected to: [" + session.getHost() + "]");

            StringBuilder error = new StringBuilder();
            // Create and connect a channel
            String command = String.join(";", commands);
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setInputStream(null);
            channel.setErrStream(null);
            channel.setCommand(command);
            channel.connect();
            InputStream inputStream = channel.getInputStream();
            InputStream errorStream = channel.getErrStream();
            channel.connect();
            // Read the output from the input stream
            byte[] buffer = new byte[1024];
            while (true) {
                while (inputStream.available() > 0) {
                    int i = inputStream.read(buffer, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    output.append(new String(buffer, 0, i));
                }
                while (errorStream.available() > 0) {
                    int i = inputStream.read(buffer, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    error.append(new String(buffer, 0, i));
                }
                if (channel.isClosed()) {
                    LOGGER.info("Exist status is: [" + channel.getExitStatus() +"]");
                    break;
                }
                waitForSeconds(10);
            }

            // Disconnect from the channel and session
            channel.disconnect();
            session.disconnect();
            if (!error.isEmpty()) {
                throw new Exception(error.toString());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return output.toString();
    }
}
