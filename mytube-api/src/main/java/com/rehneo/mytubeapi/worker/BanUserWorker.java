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
public class BanUserWorker {
    private final ExternalTaskClient client;
    private final UserService userService;

    @PostConstruct
    public void subscribe() {
        client.subscribe("ban-user")
                .lockDuration(10000)
                .handler((externalTask, externalTaskService) -> {
                    try {
                        String videoUsername = externalTask.getVariable("videoUsername");
                        User user = userService.getByUsername(videoUsername);
                        userService.banByStrikes(user);
                    } catch (Exception e) {
                        log.error("Worker 'ban-user': error", e);
                        externalTaskService.handleBpmnError(
                                externalTask,
                                "UNEXPECTED_ERROR",
                                "Ошибка при бане пользователя " + e.getMessage(),
                                Map.of("unexpectedErrorMessage", "Неожиданная ошибка: " + e.getMessage())
                        );
                    }
                })
                .open();
    }
}
