package com.rehneo.mytubeapi.worker;

import com.rehneo.mytubeapi.dto.VideoCreateDto;
import com.rehneo.mytubeapi.repository.VideoRepository;
import com.rehneo.mytubeapi.service.VideoService;
import com.rehneo.mytubeapi.user.User;
import com.rehneo.mytubeapi.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.engine.variable.value.FileValue;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class VideoUploadWorker {
    private final ExternalTaskClient client;
    private final UserService userService;
    private final VideoRepository videoRepository;
    private final VideoService videoService;

    @PostConstruct
    public void subscribe() {
        client.subscribe("upload-video")
                .lockDuration(10000)
                .handler((externalTask, externalTaskService) -> {
                    try {
                        String name = externalTask.getVariable("videoName");
                        String description = externalTask.getVariable("videoDescription");
                        String username = externalTask.getVariable("username");
                        FileValue file = externalTask.getVariableTyped("videoFile");
                        User user = userService.getByUsername(username);
                        int videoId = videoRepository.getNextVideoId();
                        videoService.save(
                                videoId,
                                new VideoCreateDto(name, description),
                                file.getValue(),
                                file.getValue().available(),
                                user
                        );
                        externalTaskService.complete(externalTask,
                                Map.of("videoId", videoId));
                    } catch (Exception e) {
                        log.error("Worker 'upload-video': error", e);
                        externalTaskService.handleBpmnError(
                                externalTask,
                                "UNEXPECTED_ERROR",
                                "Ошибка при загрузке видео " + e.getMessage(),
                                Map.of("unexpectedErrorMessage", "Неожиданная ошибка: " + e.getMessage())
                        );
                    }
                })
                .open();
    }
}
