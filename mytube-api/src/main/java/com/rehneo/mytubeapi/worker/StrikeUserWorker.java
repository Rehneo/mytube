package com.rehneo.mytubeapi.worker;

import com.rehneo.mytubeapi.domain.Strike;
import com.rehneo.mytubeapi.domain.Video;
import com.rehneo.mytubeapi.error.ResourceNotFoundException;
import com.rehneo.mytubeapi.repository.StrikeRepository;
import com.rehneo.mytubeapi.repository.VideoRepository;
import com.rehneo.mytubeapi.user.User;
import com.rehneo.mytubeapi.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Slf4j
@RequiredArgsConstructor
public class StrikeUserWorker {
    private final ExternalTaskClient client;
    private final VideoRepository videoRepository;
    private final StrikeRepository repository;
    private final UserService userService;

    @PostConstruct
    public void subscribe() {
        client.subscribe("strike-user")
                .lockDuration(10000)
                .handler((externalTask, externalTaskService) -> {
                    try {
                        Long videoId = externalTask.getVariable("videoId");
                        String strikeReason = externalTask.getVariable("strikeReason");
                        String videoUsername = externalTask.getVariable("videoUsername");
                        Video video = videoRepository.findById(videoId.intValue()).orElseThrow(
                                () -> new ResourceNotFoundException("video with id " + videoId.intValue() + " not found")
                        );
                        User user = userService.getByUsername(videoUsername);
                        int numberOfStrikes = repository.getNumberOfStrikesByUserId(user.getId());
                        Strike strike = Strike.builder()
                                .video(video)
                                .user(user)
                                .reason(strikeReason)
                                .build();
                        repository.save(strike);
                        if (numberOfStrikes >= 2) {
                            externalTaskService.complete(externalTask,
                                    Map.of("shouldBanUser", true));
                        }
                        externalTaskService.complete(externalTask,
                                Map.of("shouldBanUser", false));
                    } catch (Exception e) {
                        log.error("Worker 'strike-user': error", e);
                        externalTaskService.handleBpmnError(
                                externalTask,
                                "UNEXPECTED_ERROR",
                                "Ошибка при страйке пользователя " + e.getMessage(),
                                Map.of("unexpectedErrorMessage", "Неожиданная ошибка: " + e.getMessage())
                        );
                    }
                })
                .open();
    }
}
