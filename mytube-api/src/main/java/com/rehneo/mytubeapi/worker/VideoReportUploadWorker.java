package com.rehneo.mytubeapi.worker;

import com.rehneo.mytubeapi.dto.VideoReportCreateDto;
import com.rehneo.mytubeapi.error.ResourceNotFoundException;
import com.rehneo.mytubeapi.repository.VideoRepository;
import com.rehneo.mytubeapi.service.VideoReportService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Slf4j
@RequiredArgsConstructor
public class VideoReportUploadWorker {
    private final ExternalTaskClient client;
    private final VideoReportService videoReportService;
    private final VideoRepository videoRepository;

    @PostConstruct
    public void subscribe() {
        client.subscribe("save-video-report")
                .lockDuration(10000)
                .handler((externalTask, externalTaskService) -> {
                    try {
                        Long videoId = externalTask.getVariable("videoId");
                        String description = externalTask.getVariable("description");
                        String username = externalTask.getVariable("username");
                        String videoUsername = videoRepository.findById(videoId.intValue()).orElseThrow(
                                () -> new ResourceNotFoundException("Video with id: " + videoId + " not found")
                        ).getUser().getUsername();
                        videoReportService.report(new VideoReportCreateDto(description, videoId.intValue()), username);
                        externalTaskService.complete(externalTask, Map.of("videoUsername", videoUsername));
                    } catch (Exception e) {
                        log.error("Worker 'save-video-report': error", e);
                        externalTaskService.handleBpmnError(
                                externalTask,
                                "UNEXPECTED_ERROR",
                                "Ошибка при сохранении жалобы на видео " + e.getMessage(),
                                Map.of("unexpectedErrorMessage", "Неожиданная ошибка: " + e.getMessage())
                        );
                    }
                })
                .open();
    }
}
