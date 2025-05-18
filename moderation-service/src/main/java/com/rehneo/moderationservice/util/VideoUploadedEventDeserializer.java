package com.rehneo.moderationservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rehneo.moderationservice.event.VideoUploadedEvent;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class VideoUploadedEventDeserializer implements Deserializer<VideoUploadedEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public VideoUploadedEvent deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) return null;
        try {
            String json = new String(data, StandardCharsets.UTF_8);
            return objectMapper.readValue(json, VideoUploadedEvent.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize VideoUploadedEvent", e);
        }
    }

    @Override
    public void close() {
    }
}
