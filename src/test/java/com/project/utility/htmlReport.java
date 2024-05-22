package com.project.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class htmlReport {
    public static String testResultTextFile = "target/testResultForReport.txt";
    // Highest percentage for color coding
    public static final int redThreshold = 59;
    public static final int yellowThreshold = 84;
    public static String toResultTextFile;

    public static void main(String[] args) throws FileNotFoundException {
        String htmlReportOutputFile = "target/dashboard.html";
        //Read testResultTextFile, build a hashMap of modules with pass/fail array
        File scf = new File(testResultTextFile);
        Scanner scan = null;
        try {
            scan = new Scanner(scf);
        } catch (FileNotFoundException e) {
            // Jenkins runs have different working directory, hack workaround
            scf = new File("optum/" + testResultTextFile);
            htmlReportOutputFile = "optum/" + htmlReportOutputFile;
            scan = new Scanner(scf);
        }
        HashMap<String, int[]> hm = new HashMap<>();

        String line;
        int cntr = 0;
        while (scan.hasNextLine()) {
            line = scan.nextLine();
            if (cntr == 0) {
                //Skip the first line
                cntr++;
                continue;
            }
            String[] tempArray = line.split(",");
            String moduleName = tempArray[0];
            String status = tempArray[1];

            int[] array = hm.get(moduleName);
            // If moduleName does not yet exist is hashMap, get() returns a null array object
            // Initialize to zeros
            if (array == null) {
                array = new int[2];
            }
            if (status.equalsIgnoreCase("PASS")) {
                array[0] = array[0] + 1;
            } else {
                array[1] = array[1] + 1;
            }
            hm.put(moduleName, array);
            cntr++;
        }

        scan.close();


        //File dashboard = new File("synergy_proj/" + htmlReportOutputFile);
        File dashboard = new File(htmlReportOutputFile);

        PrintWriter dashboardWriter = new PrintWriter(dashboard);
        dashboardWriter.println("<html><head><style>table, th, td {border: 1px solid black;}</style></head></body>");

        dashboardWriter.println("<table><thead><th>Module</th><th>#Pass</th><th>#Fail</th><th>HealthStatus(Pass %)</th></thead></tbody>");
        //dashboardWriter.println(x);

        Set<Entry<String, int[]>> keySet = hm.entrySet();
        for (Entry<String, int[]> m : keySet) {
            //
            int[] a = m.getValue();
            int passPercentage = (a[0] * 100) / (a[0] + a[1]);

            String colorCode = null;

            if (passPercentage <= redThreshold) {
                colorCode = "\"#FF0000\"";
            } else if (passPercentage <= yellowThreshold) {
                colorCode = "\"#FFFF00\"";
            } else {
                colorCode = "\"#00FF00\"";
            }
            dashboardWriter.println("<tr><td>" + m.getKey() + "</td>" + "<td>" + a[0] + "</td>" + "<td>" + a[1]
                    + "</td><td> bgcolor =" + colorCode + ">" + passPercentage + "%" + "</td></tr>");
        }

        dashboardWriter.println("</tbody></table><br><br>");
        dashboardWriter.println("<table ><thead><th style=\"font-size: 12px;\">Passed Range</th><th style=\"font-size: 12px;\">Health"
                + "Indicator</th></thead><tbody"
                + "<tr><td> style=\"font-size: 10px;\">" + (yellowThreshold + 1) + "%-100%</td><td> bgcolor=\"#00FF00\"></td></tr>"
                + "<tr><td> style=\"font-size: 10px;\">" + (redThreshold + 1) + "%-" + yellowThreshold + "%</td><td> bgcolor=\"#FFFF00\"></td></tr>"
                + "<tr><td> style=\"font-size: 10px;\">0-" + (redThreshold + 1) + "%-100%</td><td> bgcolor=\"#FF0000\"></td></tr>"
                + "</table></body></html>"
        );
        dashboardWriter.close();
    }
}
