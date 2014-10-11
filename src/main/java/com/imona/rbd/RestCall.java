package com.imona.rbd;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 11.10.2014.
 *
 * @author Utku
 */
@SuppressWarnings("unchecked")
public class RestCall {
    public static Map<String, Object> deviceDetected(String branchId, String bluetoothId) {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());

        RestTemplate restTemplate = new RestTemplate(messageConverters);

        Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put("branch_id", branchId);
        urlParameters.put("bluetooth_id", bluetoothId);

        return (Map<String, Object>) restTemplate.getForObject(
                "https://www.imona.com/platform/rest/publicService/admin/testf/DeviceDetected" +
                        "?branchID={branch_id}" +
                        "&bluetoothID={bluetooth_id}", Map.class, urlParameters);
    }

    public static Map<String, Object> deviceLeft(String branchId, String bluetoothId) {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());

        RestTemplate restTemplate = new RestTemplate(messageConverters);

        Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put("branch_id", branchId);
        urlParameters.put("bluetooth_id", bluetoothId);

        return (Map<String, Object>) restTemplate.getForObject(
                "https://www.imona.com/platform/rest/publicService/admin/testf/DeviceLeft" +
                        "?branchID={branch_id}" +
                        "&bluetoothID={bluetooth_id}", Map.class, urlParameters);
    }

    public static void main(String[] args) {
        try {
            String branchId = "123456";
            String bluetoothId = "654321";

            System.out.println("Device detected Test");
            Map<String, Object> detectResult = RestCall.deviceDetected(branchId, bluetoothId);
            printResult(detectResult);

            System.out.println("-------------------------------------------------------------");

            System.out.println("Device left Test");
            Map<String, Object> leftResult = RestCall.deviceLeft(branchId, bluetoothId);
            printResult(leftResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printResult(Map<String, Object> result) {
        System.out.println("Results");
        System.out.println("-------");
        if (result == null) {
            System.out.println("null");
            return;
        }

        for (Map.Entry<String, Object> entry : result.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
