package com.project.utility;

import com.project.pages.CommonSteps;
import com.project.tests.utilities.ConfigProvider;
import com.project.utility.azure.AzureCommonStepsTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class KubectlConfig extends CommonSteps {
    private static final Logger LOGGER = LogManager.getLogger(KubectlConfig.class);

//    private static String getContextName() {
//        String environment = ConfigProvider.getAsString("Environment");
//        String contextName = null;
//        switch (environment) {
//            case "CloudStage":
//                contextName = "opastg-aks";
//                break;
//            default:
//                Assertions.fail("Environment does not exist ");
//        }
//        return contextName;
//    }

    private static String getPortForwardCommand(String serviceName) {
        String environment = ConfigProvider.getAsString("Environment");
        String command = null;
        switch (serviceName) {
            case "opay-enr-bank-account-validator":
                switch (environment) {
                    case "CloudStage":
                        command = "kubectl port-forward svc/opay-enr-bank-account-validator 7778:80 -n op-ms-stg";
                        break;
                    default:
                        Assertions.fail("Environment does not exist on KubectlConfig class.");
                }
                break;
            case "opay-enr-enrollment-processor":
                switch (environment) {
                    case "CloudStage":
                        command = "kubectl port-forward svc/opay-enr-enrollment-processor 7777:80 -n op-ms-stg";
                        break;
                    default:
                        Assertions.fail("Environment does not exist on KubectlConfig class.");
                }
                break;
            case "stageDB":
                switch (environment) {
                    case "CloudStage":
                        command = "kubectl port-forward svc/pg-values-proxy 5432:80 -n proxy";
                        break;
                    default:
                        Assertions.fail("Environment does not exist on KubectlConfig class.");
                }
                break;
            default:
                Assertions.fail("Service [" + serviceName + "] does not exist on KubectlConfig class.");
        }
        return command;
    }

    public void portForward(String serviceName) {
        String contextName = null;
        if (serviceName.equals("opay-enr-bank-account-validator") || serviceName.equals("opay-enr-enrollment-processor")) {
            contextName = "opastg-aks-admin";
        } else if (serviceName.equals("stageDB")) {
            contextName = "optumpaystg-aks";
        } else {
            LOGGER.warn("Environment does not exist on KubectlConfig class.");
            Assertions.fail("Environment does not exist on KubectlConfig class.");
        }

        String[] commands = new String[]{
                String.format("kubectl config use-context %s", contextName),
                getPortForwardCommand(serviceName)
        };
        // Execute commands
        try {
            Process process = Runtime.getRuntime().exec(commands);
            printProcessOutput(process);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        LOGGER.info("Port forward complete.");
    }

    private static void printProcessOutput(Process process) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            LOGGER.info(line);
            break;
        }
    }
}
