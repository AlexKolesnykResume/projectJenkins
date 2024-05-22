package com.project.tests.utilities;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class SendMail {
    private static final Logger LOGGER = LogManager.getLogger(SendMail.class);

    private static final String HOST = ConfigProvider.getAsString("host");
    private static final String FROM_AUTOMATION = "someEmail@gmail.com";
    private static final String SECRET = "someSecret";
    private static final String timeStamp = "[" + ZonedDateTime.now(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("HH.mm.ss")) + "]";
    private static String content = null;

    public static void main(String[] args) {
        if (ConfigProvider.getAsBoolean("sendEmail")) {
            LOGGER.info("Start composing email.");

            content = "<html><head></head><body><p style=\"font-size:120%;\">Hi All,\r\n" +
                    " <br><br>\r\n" +
                    "Below are test executions results. <br>\r\n" +
                    "Please click on Smoke link to see the scenarios, detailed steps and any failure screenshots\r\n" +
                    " <br><br>\r\n" +
                    "Browser Type: <b>" + ExtractTestResultsFromLog.getMavenOptions(SystemVsConfigProp.SystemPropertyVariables.Browser.name()) + "</b><br>\r\n" +
                    "Environment: <b>" + ExtractTestResultsFromLog.getMavenOptions(SystemVsConfigProp.SystemPropertyVariables.Environment.name()) + "</b><br>\r\n" +
                    "ApplicationUrl: <a href=" + ExtractTestResultsFromLog.getMavenOptions(SystemVsConfigProp.SystemPropertyVariables.GOOGLEURL.name()) + ">" + ExtractTestResultsFromLog.getMavenOptions(SystemVsConfigProp.SystemPropertyVariables.GOOGLEURL.name()) + "</a><br>\r\n" +
                    "Deployment Version: <b>" + "ExtractTestResultsFromLog.getDeployedVersion()" + "</b>\r\n" +
                    "\r\n</b><br>\r\n\r\n" + "<head>\r\n" +
                    "<style>\r\n#custometrs {\r\nfont-family: Arial, Helvetica, sans-serif;\r\n" +
                    "  border-collapse: collapse;\r\n  width: 50%;\r\n}\r\n\r\n" +
                    "#customers td, #customers th {\r\n  border: 1px solid #ddd;\r\n" +
                    "  border-color: black;\r\n  padding: 10px;\r\n}\r\n\r\n" +
                    "#customers tr:nth-child(even){background-color: #f2f2f2;}\r\n\r\n" +
                    "#customers tr:hover {background-color: #ddd;}\r\n\r\n #customers th {\r\n" +
                    "  padding-top: 6px; \r\n  padding-bottom: 6px;\r\n  text-align: left;\r\n" +
                    "  background-color: #C25608;\r\n  color: white;\r\n}\r\n</style>\r\n</head>\r\n" +
                    "<body>\r\n<table id=\"customers\">\r\n  <tr>\r\n" +
                    "    <th>Module</th>\r\n    <th>#Pass</th>\r\n    <th>#Fail</th>\r\n    <th>#Skipped</th>\r\n    <th>Total Count</th>\r\n  </tr>\r\n" +
                    "<tr>\r\n    <td><b>Smoke</b></a></td>\r\n" +
                    "    <td style='color:green;'><b>" + ExtractTestResultsFromLog.get("Pass") + "</b></td>\r\n" +
                    "    <td style='color:red;'><b>" + ExtractTestResultsFromLog.get("Fail") + "</b></td>\r\n" +
                    "    <td style='color:orange;'><b>" + ExtractTestResultsFromLog.get("Skipped") + "</b></td>\r\n" +
                    "    <td><b>" + ExtractTestResultsFromLog.get("Total Count") + "</b></td>\r\n  </tr>\r\n  </tr>\r\n" +
                    "</table>\r\n\r\n</body>\r\n<br> \r\n<p style=\"font-size:120%;\">\r\n" +
                    "<b>Build was initiated by: <font color=\"blue\">" + System.getProperty("user.name") + "</font></b>\r\n<br>\r\n" +
                    "<b>Build Status# : <font color=\"blue\">" + ExtractTestResultsFromLog.get("Build Status") + "</font></b>\r\n<br>\r\n<bt>\r\n" +
                    "\r\nThank you, <br>\r\nAutomation Team</p></body></html>";
            sendEmail();
        }
    }

    public static void sendEmail() {
        String from = FROM_AUTOMATION;
        String[] to = {"someOtherEmail@gmail.com", "someOtherEmail@gmail.com"};
        String subject = "Automation Report";
        String body = "Below is the automation report :";
        send(from, to, subject, body);
    }

    private static void send(String from, String[] to, String subject, String body) {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", HOST);

        Session session = Session.getDefaultInstance(properties);

//        session.setDebug(true);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from));
            InternetAddress[] toAddress = new InternetAddress[to.length];
            // To get the array of addresses
            for (int i = 0; i < to.length; i++) {
                toAddress[i] = new InternetAddress(to[i]);
            }
            for (InternetAddress address : toAddress) {
                message.addRecipient(Message.RecipientType.TO, address);
            }

            // Set subject of email
            message.setSubject(subject);
            message.setHeader("x-OPTUM360-Azure", "Yes");


            if (ConfigProvider.getAsBoolean("sendWithAttachment")) {
                ZipFolder.zipExecutionReport();
                Multipart multipart = new MimeMultipart();

                MimeBodyPart textBodyPart = new MimeBodyPart();
                textBodyPart.setContent(content, "text/html");
                MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(System.getProperty("user.dir") + File.separator + "AutomationReport.zip");
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName("AutomationReport.zip");

                multipart.addBodyPart(textBodyPart);
                multipart.addBodyPart(attachmentBodyPart);
                message.setContent(multipart);
            } else {
                message.setContent(content, "text/html");
            }

            Transport transport = session.getTransport("smtp");
            transport.connect(HOST, from);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            System.out.println("Email sent!");
        } catch (MessagingException e) {
            System.out.println("Failed to send email.");
        }
    }
}
