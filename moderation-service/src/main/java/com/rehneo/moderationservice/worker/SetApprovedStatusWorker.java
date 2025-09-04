package com.rehneo.moderationservice.worker;

import com.rehneo.moderationservice.domain.ModerationStatus;
import com.rehneo.moderationservice.domain.Video;
import com.rehneo.moderationservice.repository.VideoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Slf4j
@RequiredArgsConstructor
public class SetApprovedStatusWorker {
    private final ExternalTaskClient client;
    private final VideoRepository videoRepository;

    @PostConstruct
    public void subscribe() {
        client.subscribe("set-approved-status")
                .lockDuration(10000)
                .handler((externalTask, externalTaskService) -> {
                    try {
                        int videoId = externalTask.getVariable("videoId");
                        Video video = videoRepository.findById(videoId).orElseThrow();
                        video.setModerationStatus(ModerationStatus.PASSED);
                        videoRepository.save(video);
                        externalTaskService.complete(externalTask);
                        log.info("Finished processing video with id: {}", videoId);
                    } catch (Exception e) {
                        log.error("Worker 'set-approved-status': error", e);
                        externalTaskService.handleBpmnError(
                                externalTask,
                                "UNEXPECTED_ERROR",
                                "Ошибка при изменении статуса видео " + e.getMessage(),
                                Map.of("unexpectedErrorMessage", "Неожиданная ошибка: " + e.getMessage())
                        );
                    }
                })
                .open();
    }
}
