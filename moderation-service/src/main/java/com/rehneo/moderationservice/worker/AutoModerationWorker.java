package com.rehneo.moderationservice.worker;

import com.rehneo.moderationservice.service.VideoModerationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Slf4j
@RequiredArgsConstructor
public class AutoModerationWorker {
    private final ExternalTaskClient client;
    private final VideoModerationService moderationService;

    @PostConstruct
    public void subscribe() {
        client.subscribe("auto-moderation")
                .lockDuration(10000)
                .handler((externalTask, externalTaskService) -> {
                    try {
                        int id = externalTask.getVariable("videoId");
                        boolean shouldRejectVideo = moderationService.processModeration(id);
                        externalTaskService.complete(externalTask, Map.of(
                                "shouldRejectVideo", shouldRejectVideo
                        ));
                    } catch (Exception e) {
                        log.error("Worker 'auto-moderation': error", e);
                        externalTaskService.handleBpmnError(
                                externalTask,
                                "UNEXPECTED_ERROR",
                                "Ошибка при автомодерации видео " + e.getMessage(),
                                Map.of("unexpectedErrorMessage", "Неожиданная ошибка: " + e.getMessage())
                        );
                    }
                })
                .open();
    }
}
