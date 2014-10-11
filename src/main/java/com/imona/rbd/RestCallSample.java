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
public class RestCallSample {
    public static void main(String[] args) {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());

        RestTemplate restTemplate = new RestTemplate(messageConverters);

        Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put("branch_id", "123456");
        urlParameters.put("bluetooth_id", "654321");

        Map<String, Object> result = (Map<String, Object>) restTemplate.getForObject(
                "https://www.imona.com/platform/rest/publicService/admin/testf/DeviceDetected" +
                        "?branchID={branch_id}" +
                        "&bluetoothID={bluetooth_id}", Map.class, urlParameters);

        for (Map.Entry<String, Object> entry : result.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
