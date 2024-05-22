package com.project;

import com.project.pages.CommonSteps;
import com.project.tests.utilities.ExtractTestResultsFromLog;
import com.project.tests.utilities.Log;
import com.project.tests.utilities.SystemVsConfigProp;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.util.Properties;

public class PlaygroundTest extends CommonSteps {
    private static final Logger LOGGER = LogManager.getLogger(PlaygroundTest.class);
    private static final String htmlContent = buildHtmlContent();

    private static String buildHtmlContent() {
        String tagName = ExtractTestResultsFromLog.getMavenOptions(SystemVsConfigProp.SystemPropertyVariables.TagName.name());
        String browserType = ExtractTestResultsFromLog.getMavenOptions(SystemVsConfigProp.SystemPropertyVariables.Browser.name());
        String environment = ExtractTestResultsFromLog.getMavenOptions(SystemVsConfigProp.SystemPropertyVariables.Environment.name());
        String appUrl = ExtractTestResultsFromLog.getMavenOptions(SystemVsConfigProp.SystemPropertyVariables.GOOGLEURL.name());
        String deploymentVersion = ExtractTestResultsFromLog.getMavenOptions(SystemVsConfigProp.SystemPropertyVariables.DeploymentVersion.name());
        String passedTests = ExtractTestResultsFromLog.getMavenOptions(SystemVsConfigProp.SystemPropertyVariables.PassedTests.name());
        String failedTests = ExtractTestResultsFromLog.getMavenOptions(SystemVsConfigProp.SystemPropertyVariables.FailedTests.name());
        String skippedTests = ExtractTestResultsFromLog.getMavenOptions(SystemVsConfigProp.SystemPropertyVariables.SkippedTests.name());
        String totalTests = ExtractTestResultsFromLog.getMavenOptions(SystemVsConfigProp.SystemPropertyVariables.TotalTests.name());
        String buildStatus = ExtractTestResultsFromLog.getMavenOptions(SystemVsConfigProp.SystemPropertyVariables.BuildStatus.name());

        String userName = System.getProperty("user.name");

        StringBuilder htmlBuilder = new StringBuilder();

        htmlBuilder.append("<html><head><style>");
        htmlBuilder.append("body {font-family: 'Arial', sans-serif; margin: 20px; font-size: 18px;}");
        htmlBuilder.append("h1 {color: #333;}");
        htmlBuilder.append("p {font-size: 16px; color: #555;}");
        htmlBuilder.append("table {border-collapse: collapse; width: 50%;}");
        htmlBuilder.append("th, td {border: 1px solid #ddd; border-color: black; padding: 12px; text-align: left;}");
        htmlBuilder.append("th {font-size: 20px;}");
        htmlBuilder.append("td {font-size: 15px;}");

        if (buildStatus.equals("All tests are passing")) {
            htmlBuilder.append("th.passed {background-color: #4CAF50; color: white;}");
            htmlBuilder.append("th.failed {background-color: #4CAF50; color: white;}");
            htmlBuilder.append("th.skipped {background-color: #4CAF50; color: white;}");
            htmlBuilder.append("th.total {background-color: #4CAF50; color: white;}");
        } else {
            htmlBuilder.append("th.passed {background-color: #e74c3c; color: white;}");
            htmlBuilder.append("th.failed {background-color: #e74c3c; color: white;}");
            htmlBuilder.append("th.skipped {background-color: #e74c3c; color: white;}");
            htmlBuilder.append("th.total {background-color: #e74c3c; color: white;}");
            htmlBuilder.append("#tableHeaders:hover th.passed, #tableHeaders:hover th.failed, #tableHeaders:hover th.skipped, #tableHeaders:hover th.total {background-color: red;}");
        }
        htmlBuilder.append("a {text-decoration: none; color: #3498db;}");
        htmlBuilder.append("a:hover {color: #1b6ca8;}");
        htmlBuilder.append("</style></head><body>");

        // Content
        htmlBuilder.append("<h1>Test Execution Results</h1>");
        htmlBuilder.append("<p>Hi All,<br>Below are the test execution results. Click on the Tag link to see detailed scenarios, steps, and failure screenshots.</p>");
        htmlBuilder.append("<p><strong>Browser Type:</strong> ").append(browserType).append("<br>");
        htmlBuilder.append("<strong>Environment:</strong> ").append(environment).append("<br>");
        htmlBuilder.append("<strong>Application URL:</strong> <a href=\"").append(appUrl).append("\">").append(appUrl.substring(12)).append("</a><br>");
        htmlBuilder.append("<strong>Deployment Version:</strong> ").append(deploymentVersion).append("</p>");

        // Table
        htmlBuilder.append("<table id='tableHeaders'><thead><tr><th class='passed'>Tag</th><th class='passed'>#Pass</th><th class='failed'>#Fail</th><th class='skipped'>#Skipped</th><th class='total'>#Total</th></tr></thead>");
        htmlBuilder.append("<tbody><tr><td><a href=\"").append(appUrl).append("\"><strong>").append(tagName).append("</strong></a></td>");
        htmlBuilder.append("<td class='passed'><strong style='color: green;'>").append(passedTests).append("</strong></td>");
        htmlBuilder.append("<td class='failed'><strong style='color: red;'>").append(failedTests).append("</strong></td>");
        htmlBuilder.append("<td class='skipped'><strong style='color: #f39c12;'>").append(skippedTests).append("</strong></td>");
        htmlBuilder.append("<td class='total'><strong style='color: #3498db;'>").append(totalTests).append("</strong></td></tr></tbody></table>");

        // Additional Info
        htmlBuilder.append("<p><strong>Build was initiated by:</strong> <span style=\"color: #3498db;\">").append(userName).append("</span><br>");
        htmlBuilder.append("<strong>Build Status#:</strong> <span style=\"color: #3498db;\">").append(buildStatus).append("</span></p>");
        htmlBuilder.append("Status").append("</span></p>");
        htmlBuilder.append("<p>Thank you,<br>Automation Team</p></body></html>");

        return htmlBuilder.toString();
    }

    // IMPORTANT: This works!
    public static void sendEmail(String[] to, String subject, String body) {
        // Set your email properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.mail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Set your username and password
        String username = "Emailfifth@mail.com";
        String password = "Fifth1234!";
//        sendEmailFromProject
        // Create a session with authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a MimeMessage
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            InternetAddress[] toAddress = new InternetAddress[to.length];
            // To get the array of addresses
            for (int i = 0; i < to.length; i++) {
                toAddress[i] = new InternetAddress(to[i]);
            }
            for (InternetAddress address : toAddress) {
                message.addRecipient(Message.RecipientType.TO, address);
            }

            // Set the email subject and body
            message.setSubject(subject);

//            ZipFolder.zipExecutionReport();
            Multipart multipart = new MimeMultipart();

            // Text
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setContent(body, "text/html");

            //HTML text
            MimeBodyPart htmlBodyPart = new MimeBodyPart();
            htmlBodyPart.setContent(htmlContent, "text/html");

            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            DataSource sourceZip = new FileDataSource(System.getProperty("user.dir") + File.separator + "AutomationReport1.zip");
            DataSource sourceImage = new FileDataSource(System.getProperty("user.dir") + File.separator + "InTheTetons.jpg");
            attachmentBodyPart.setDataHandler(new DataHandler(sourceImage));
            attachmentBodyPart.setFileName("InTheTetons.jpg");

            // Add the PDF attachment
            MimeBodyPart attachmentBodyPartPDF = new MimeBodyPart();
            DataSource sourcePDF = new FileDataSource(System.getProperty("user.dir") + "/AutomationReports/PdfReport/ExtentPdf.pdf");
            attachmentBodyPartPDF.setDataHandler(new DataHandler(sourcePDF));
            attachmentBodyPartPDF.setFileName("ReportPDF.pdf");

            // Add the logToBeUpdated.log attachment
            MimeBodyPart attachmentBodyPartLog = new MimeBodyPart();
            DataSource sourceLog = new FileDataSource(System.getProperty("user.dir") + "/log/logToBeUpdated.log");
            attachmentBodyPartLog.setDataHandler(new DataHandler(sourceLog));
            attachmentBodyPartLog.setFileName("Log.txt");

            multipart.addBodyPart(textBodyPart);
            multipart.addBodyPart(htmlBodyPart);
            multipart.addBodyPart(attachmentBodyPart);
            multipart.addBodyPart(attachmentBodyPartPDF);
            multipart.addBodyPart(attachmentBodyPartLog);
            message.setContent(multipart);

            // Send the email
            Transport.send(message);
            Log.info(LOGGER, "Report email sent successfully!");
        } catch (MessagingException e) {
            Log.error(LOGGER, "Failed to send email. Exception: " + e.getMessage());
            Assertions.fail("Failed to send email. Exception: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        sendEmail(new String[]{"thisemailisfortestingapps@gmail.com"}, "What's up bud?!", "This is a test email from the automation.");
        System.out.println(System.getProperty("user.dir"));
    }


}
