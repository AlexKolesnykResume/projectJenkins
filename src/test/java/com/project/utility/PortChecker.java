package com.project.utility;

import com.project.pages.CommonSteps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class PortChecker extends CommonSteps {
    private static final Logger LOGGER = LogManager.getLogger(PortChecker.class);

    public void checkPort(int port) {
        if (isPortInUse(port)) {
            killProcessOnPort(port);
        }
    }

    public boolean isPortInUse(int port) {
        try {
            Socket socket = new Socket(InetAddress.getByName("localhost"), port);
            socket.close();
            LOGGER.info("Port [" + port +"] is in use.");
            return true;
        } catch (Exception e) {
            LOGGER.info("Port [" + port +"] is available.");
            return false;
        }
    }

    private static void killProcessOnPort(int port) {
        String os = System.clearProperty("os.name").toLowerCase();
        try {
            String command;
            if (os.contains("win")) {
                command = "cmd /c netstat -ano | findstr :" + port;
            } else if (os.contains("mac") || os.contains("nux") || os.contains("nix")) {
                command = "lsof -i :" + port;
            } else {
                throw  new UnsupportedOperationException("Operating system not supported.");
            }

            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("LISTEN")) {
                    String[] parts = line.split("\\s+");
                    String pid;
                    if (os.contains("win")) {
                        pid = parts[4];
                    } else {
                        pid = parts[1];
                    }

                    String killCommand = os.equals("win") ? "cmd /c taskkill /F /PID " + pid : "kill " + pid;
                    Runtime.getRuntime().exec(killCommand);
                    LOGGER.info("Process with PID [" + pid + "] was killed.");
                    break;
                }
            }
            reader.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
