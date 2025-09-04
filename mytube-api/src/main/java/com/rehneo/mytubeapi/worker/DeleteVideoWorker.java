package com.rehneo.mytubeapi.worker;

import com.rehneo.mytubeapi.service.VideoService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Slf4j
@RequiredArgsConstructor
public class DeleteVideoWorker {
    private final ExternalTaskClient client;
    private final VideoService videoService;

    @PostConstruct
    public void subscribe() {
        client.subscribe("delete-video")
                .lockDuration(10000)
                .handler((externalTask, externalTaskService) -> {
                    try {
                        Long videoId = externalTask.getVariable("videoId");
                        String reason = externalTask.getVariable("deleteReason");
                        videoService.deleteByAdmin(videoId.intValue(), reason);
                        externalTaskService.complete(externalTask);
                    } catch (Exception e) {
                        log.error("Worker 'delete-video': error", e);
                        externalTaskService.handleBpmnError(
                                externalTask,
                                "UNEXPECTED_ERROR",
                                "Ошибка при удалении видео " + e.getMessage(),
                                Map.of("unexpectedErrorMessage", "Неожиданная ошибка: " + e.getMessage())
                        );
                    }
                })
                .open();
    }
}
