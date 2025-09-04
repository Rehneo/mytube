package com.rehneo.mytubeapi.worker;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.engine.variable.value.FileValue;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class VideoFileValidationWorker {
    private final ExternalTaskClient client;

    @PostConstruct
    public void subscribe() {
        client.subscribe("video-file-validation")
                .lockDuration(10000)
                .handler((externalTask, externalTaskService) -> {
                    try {
                        boolean validationNotSuccessful = false;
                        FileValue file = externalTask.getVariableTyped("videoFile");
                        String mimeType = file.getMimeType();
                        long size = file.getValue().available();
                        if (size <= 0) {
                            validationNotSuccessful = true;
                        }
                        var extension = FilenameUtils.getExtension(file.getFilename());
                        if (extension == null || !Objects.equals(extension.toLowerCase(), "mp4") || !"video/mp4".equalsIgnoreCase(mimeType)) {
                            validationNotSuccessful = true;
                        }
                        externalTaskService.complete(externalTask, Map.of(
                                "validationNotSuccessful", validationNotSuccessful,
                                "validationErrorMessage", "Неподходящий формат, проверьте файл с видео"
                        ));
                    } catch (Exception e) {
                        log.error("Worker 'video-file-validation': error", e);
                        externalTaskService.handleBpmnError(
                                externalTask,
                                "UNEXPECTED_ERROR",
                                "Ошибка при валидации файла с видео " + e.getMessage(),
                                Map.of("unexpectedErrorMessage", "Неожиданная ошибка: " + e.getMessage())
                        );
                    }
                })
                .open();
    }
}
