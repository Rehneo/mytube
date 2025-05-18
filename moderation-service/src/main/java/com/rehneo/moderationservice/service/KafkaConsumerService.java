package com.rehneo.moderationservice.service;

import com.rehneo.moderationservice.event.VideoUploadedEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final VideoModerationService videoModerationService;

    private final KafkaConsumer<String, VideoUploadedEvent> kafkaConsumer;
    @Value("${spring.kafka.topic}")
    private String topic;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean running = true;

    @PostConstruct
    public void start() {
        kafkaConsumer.subscribe(Collections.singletonList(topic));
        executor.submit(() -> {
            try {
                while (running) {
                    ConsumerRecords<String, VideoUploadedEvent> records = kafkaConsumer.poll(Duration.ofSeconds(1));
                    for (ConsumerRecord<String, VideoUploadedEvent> record : records) {
                        try {
                            log.info("Received video with id: {}, starting processing", record.value().getVideoId());
                            videoModerationService.processModeration(record.value().getVideoId());
                            log.info("Finished processing video with id: {}", record.value().getVideoId());
                        } catch (Exception processingEx) {
                            log.error("Failed processing video id {}: ", record.value().getVideoId(), processingEx);
                        }
                    }
                }
            } catch (WakeupException e) {
                if (running) throw e;
            } catch (Exception e) {
                log.error("Error polling videos", e);
            } finally {
                kafkaConsumer.close();
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        kafkaConsumer.wakeup();
        executor.shutdown();
    }
}
