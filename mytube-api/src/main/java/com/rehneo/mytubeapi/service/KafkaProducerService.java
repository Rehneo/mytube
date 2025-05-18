package com.rehneo.mytubeapi.service;

import com.rehneo.mytubeapi.event.VideoUploadedEvent;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaProducer<String, VideoUploadedEvent> kafkaProducer;
    @Value("${spring.kafka.topic}")
    private String topic;


    public void sendVideoUploadedEvent(Integer videoId) {
        VideoUploadedEvent event = new VideoUploadedEvent(videoId);
        ProducerRecord<String, VideoUploadedEvent> record = new ProducerRecord<>(
                topic,
                null,
                event
        );
        kafkaProducer.send(record, (metadata, exception) -> {
            if (exception == null) {
                log.info("Successfully sent video uploaded event to {}. Video id: {}", record.topic(), videoId);
            } else {
                log.error("Failed to send video uploaded event to {}. Video id: {}", record.topic(), videoId);
            }
        });
    }

    @PreDestroy
    public void closeProducer() {
        kafkaProducer.close();
    }
}
