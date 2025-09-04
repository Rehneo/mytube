package com.rehneo.mytubeapi.worker;

import com.rehneo.mytubeapi.service.TextValidationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class VideoMetaValidationWorker {
    private final ExternalTaskClient client;
    private final TextValidationService validationService;

    @PostConstruct
    public void subscribe() {
        client.subscribe("video-meta-validation")
                .lockDuration(10000)
                .handler((externalTask, externalTaskService) -> {
                    try {
                        String name = externalTask.getVariable("videoName");
                        String description = externalTask.getVariable("videoDescription");
                        boolean validationNotSuccessful = validationService.containsForbiddenWord(name + description);
                        externalTaskService.complete(externalTask, Map.of(
                                "validationNotSuccessful", validationNotSuccessful,
                                "validationErrorMessage", "Название или описание видео содержат запрещенные слова"
                        ));
                    } catch (Exception e) {
                        log.error("Worker 'video-meta-validation': error", e);
                        externalTaskService.handleBpmnError(
                                externalTask,
                                "UNEXPECTED_ERROR",
                                "Ошибка при валидации описания и названия видео " + e.getMessage(),
                                Map.of("unexpectedErrorMessage", "Неожиданная ошибка: " + e.getMessage())
                        );
                    }
                })
                .open();
    }
}
