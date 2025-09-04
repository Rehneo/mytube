package com.rehneo.mytubeapi.worker;

import com.rehneo.mytubeapi.service.RejectedVideosDeleteService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Slf4j
@RequiredArgsConstructor
public class RejectedVideosDeleteWorker {
    private final ExternalTaskClient client;
    private final RejectedVideosDeleteService rejectedVideosDeleteService;

    @PostConstruct
    public void subscribe() {
        client.subscribe("rejected-videos-delete-job")
                .lockDuration(10000)
                .handler((externalTask, externalTaskService) -> {
                    try {
                        log.info("Starting rejected videos cleanup job");
                        rejectedVideosDeleteService.execute();
                        log.info("Finished rejected videos cleanup job");
                        externalTaskService.complete(externalTask);
                    } catch (Exception e) {
                        log.error("Worker 'rejected-videos-delete-job': error", e);
                        externalTaskService.handleBpmnError(
                                externalTask,
                                "UNEXPECTED_ERROR",
                                "Ошибка при удалении видео с нарушением правил" + e.getMessage(),
                                Map.of("unexpectedErrorMessage", "Неожиданная ошибка: " + e.getMessage())
                        );
                    }
                })
                .open();
    }
}