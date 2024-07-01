package com.len.messaging.jms;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Map;

// mit dem eingebetteten JMS-Broker nicht möglich
@Component
public class JmsMetrics {

    @Value("${jolokia.url}")
    private String jolokiaUrl;

    @Value("${jolokia.username}")
    private String jolokiaUsername;

    @Value("${jolokia.password}")
    private String jolokiaPassword;

    public int getQueueSize(String queueName) {
        try {
            String url = String.format(jolokiaUrl, queueName);
            RestTemplate restTemplate = new RestTemplate();

            // Anmeldeinformationen in den Header aufnehmen
            HttpHeaders headers = new HttpHeaders();
            String auth = jolokiaUsername + ":" + jolokiaPassword;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeader = "Basic " + encodedAuth;
            headers.set("Authorization", authHeader);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            System.out.println(response);

            // JSON-Antwort ausgeben
            String responseBodyString = response.getBody();
            System.out.println("Response Body: " + responseBodyString);

            // JSON-Antwort parsen
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseBody = objectMapper.readValue(responseBodyString, Map.class);
            System.out.println(responseBody);
            // Den Wert der QueueSize extrahieren
            if (responseBody.containsKey("value")) {
                Map<String, Object> valueMap = (Map<String, Object>) responseBody.get("value");
                if (valueMap.containsKey("QueueSize")) {
                    return (Integer) valueMap.get("QueueSize");
                } else {
                    System.out.println("Key 'QueueSize' not found in the 'value' map");
                    return -1;
                }
            } else {
                System.out.println("Key 'value' not found in the response body");
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Fehlerbehandlung
        }
    }


}