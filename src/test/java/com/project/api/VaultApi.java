package com.project.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.pageObjectManager.PageObjectManager;
import com.project.pages.CommonSteps;
import com.project.pages.GoogleSearchPage;
import com.project.tests.utilities.ConfigProvider;
import com.project.tests.utilities.Driver;
import io.restassured.response.Response;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.JavascriptExecutor;

import java.util.Map;

public class VaultApi {
    private static Map<String, String> vaultMap = null;
//
//    public static Map<String, String> requestVaultJS() {
//        String vaultEndpoint = ConfigProvider.getAsString("vaultEndPoint");
//        String decodedKey = CommonSteps.decodeBase64(ConfigProvider.getAsString("vaultKey"));
//        PageObjectManager.get(GoogleSearchPage.class);
//        Driver.getDriver().navigate().to("https://pam-dev.uhc.com");
//        JavascriptExecutor executor = Driver.getDriver();
//        String rawResponse = (String) executor.executeScript("var callback = arguments[arguments.length - 1]; var myHeaders = new Headers(); myHeaders.append(\"accept\", \"*/*\"); myHeaders.append(\"X-Vault-Token\", \"" + decodedKey + "\"); myHeaders.append(\"X-Vault-Namespace\", \"OPTUM/APP/CPS/STAGE\"); myHeaders.append(\"Cookie\", \"BIGipServerpam-dev.uhc.com_8200=1882245386.2080.0000\"); var requestOptions = { method: 'GET', headers: myHeaders, redirect: 'follow'}; return fetch(\"" + vaultEndpoint + "\", requestOptions).then(response => response.text()).then(result => callback(result)).catch(error => console.log('error', error));");
//        Map<String, String> result = null;
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            JSONObject responseObject = mapper.readValue(rawResponse, JSONObject.class);
//            result = (Map<String, String>) ((Map<String, Object>) responseObject.get("data")).get("data");
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    public static void printStatusCode(Response response) {
//        int actualStatusCode = response.getStatusCode();
//        System.out.println("Vault API status code is: [" + actualStatusCode + " --> " + CommonMethods.getStatusCodeName(actualStatusCode) + "].");
//    }
//
//    public static String getVaultValueForKey(String key) {
//        Map<String, String> vaultMap = getVaultMap();
//        if (vaultMap.get(key) == null) {
//            System.out.println("Key [" + key + "] does not exist in the Vault API, please check.");
//            Assertions.fail("Key [" + key + "] does not exist in the Vault API, please check.");
//        }
//        return vaultMap.get(key);
//    }
//
//    public static Map<String, String> getVaultMap() {
//        return vaultMap == null ? vaultMap = requestVaultJS() : vaultMap;
//    }
}
