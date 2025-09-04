package com.rehneo.mytubeapi.worker;

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
public class CheckUserBanWorker {
    private final ExternalTaskClient client;
    private final UserService userService;

    @PostConstruct
    public void subscribe() {
        client.subscribe("check-user-ban")
                .lockDuration(10000)
                .handler((externalTask, externalTaskService) -> {
                    try {
                        String videoUsername = externalTask.getVariable("videoUsername");
                        User user = userService.getByUsername(videoUsername);
                        boolean userIsBanned = userService.isBanned(user);
                        externalTaskService.complete(externalTask,
                                Map.of("userIsBanned", userIsBanned));
                    } catch (Exception e) {
                        log.error("Worker 'check-user-ban': error", e);
                        externalTaskService.handleBpmnError(
                                externalTask,
                                "UNEXPECTED_ERROR",
                                "Ошибка при проверке бана пользователя " + e.getMessage(),
                                Map.of("unexpectedErrorMessage", "Неожиданная ошибка: " + e.getMessage())
                        );
                    }
                })
                .open();
    }
}
