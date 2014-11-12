package com.imona.rbd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static Logger logger = LoggerFactory.getLogger(RestCall.class);

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

            logger.info("Device detected Test");
            Map<String, Object> detectResult = RestCall.deviceDetected(branchId, bluetoothId);
            printResult(detectResult);

            logger.info("-------------------------------------------------------------");

            logger.info("Device left Test");
            Map<String, Object> leftResult = RestCall.deviceLeft(branchId, bluetoothId);
            printResult(leftResult);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void printResult(Map<String, Object> result) {
        logger.info("Results");
        logger.info("-------");
        if (result == null) {
            logger.info("null");
            return;
        }

        for (Map.Entry<String, Object> entry : result.entrySet()) {
            logger.info(entry.getKey() + ": " + entry.getValue());
        }
    }
}
