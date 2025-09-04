package com.rehneo.mytubeapi.worker;

import com.rehneo.mytubeapi.service.KafkaProducerService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Slf4j
@RequiredArgsConstructor
public class SendMessageKafkaWorker {
    private final ExternalTaskClient client;
    private final KafkaProducerService kafkaProducerService;

    @PostConstruct
    public void subscribe() {
        client.subscribe("send-moderation-request")
                .lockDuration(10000)
                .handler((externalTask, externalTaskService) -> {
                    try {
                        int videoId = externalTask.getVariable("videoId");
                        kafkaProducerService.sendVideoUploadedEvent(videoId);
                        externalTaskService.complete(externalTask);
                    } catch (Exception e) {
                        log.error("Worker 'send-moderation-request': error", e);
                        externalTaskService.handleBpmnError(
                                externalTask,
                                "UNEXPECTED_ERROR",
                                "Ошибка при отправке сообщения " + e.getMessage(),
                                Map.of("unexpectedErrorMessage", "Неожиданная ошибка: " + e.getMessage())
                        );
                    }
                })
                .open();
    }
}
