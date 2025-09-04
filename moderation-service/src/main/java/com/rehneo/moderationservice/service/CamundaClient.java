package com.rehneo.moderationservice.service;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CamundaClient {

    private final RestTemplate restTemplate;

    public void sendMessage(Integer videoId) {
        String url = "http://localhost:8088/engine-rest/message";

        Map<String, Object> body = getObjectMap(videoId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(url, request, String.class);
    }

    @NotNull
    private static Map<String, Object> getObjectMap(Integer videoId) {
        Map<String, Object> body = new HashMap<>();
        body.put("messageName", "ModerationRequestMessage");
        body.put("processVariables", getCorrelation(videoId));
        body.put("correlationKeys", getCorrelation(videoId));
        return body;
    }

    @NotNull
    private static Map<String, Object> getCorrelation(Integer videoId) {
        Map<String, Object> correlationKeyValue = new HashMap<>();
        correlationKeyValue.put("value", videoId);
        correlationKeyValue.put("type", "Integer");

        Map<String, Object> correlationKeys = new HashMap<>();
        correlationKeys.put("videoId", correlationKeyValue);

        return correlationKeys;
    }
}
