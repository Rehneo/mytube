package com.rehneo.mytubeapi.worker;

import com.rehneo.mytubeapi.auth.AuthRequest;
import com.rehneo.mytubeapi.auth.AuthResponse;
import com.rehneo.mytubeapi.auth.AuthService;
import com.rehneo.mytubeapi.user.UserNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticateUserWorker {
    private final ExternalTaskClient client;
    private final AuthService authService;

    @PostConstruct
    public void subscribe() {
        client.subscribe("authenticate-user")
                .lockDuration(10000)
                .handler((externalTask, externalTaskService) -> {
                    String username = externalTask.getVariable("username");
                    String password = externalTask.getVariable("password");

                    log.info("Worker 'authenticate-user': processing task for user '{}'", username);

                    try {
                        AuthRequest authReq = new AuthRequest(username, password);
                        AuthResponse userResp = authService.signIn(authReq);

                        Map<String, Object> variablesToSet = Map.of(
                                "jwt", userResp.getToken(),
                                "authenticationSuccessful", true
                        );
                        externalTaskService.complete(externalTask, variablesToSet);
                        log.info("Worker 'authenticate-user': authentication successful for '{}', token issued", username);

                    } catch (AuthenticationException e) {
                        log.warn("Worker 'authenticate-user': invalid credentials for user '{}'", username);
                        String errorMessage = "Неверный логин или пароль.";
                        externalTaskService.handleBpmnError(
                                externalTask,
                                "AUTH_FAILED",
                                errorMessage,
                                Map.of(
                                        "authErrorMessage", errorMessage,
                                        "authenticationSuccessful", false
                                )
                        );
                    } catch (UserNotFoundException e) {
                        log.warn("Worker 'authenticate-user': User not found for username '{}'", username);
                        String errorMessage = "Пользователь с логином " + username + " не найден";
                        externalTaskService.handleBpmnError(
                                externalTask,
                                "AUTH_FAILED",
                                errorMessage,
                                Map.of(
                                        "authErrorMessage", errorMessage,
                                        "authenticationSuccessful", false
                                )
                        );
                    } catch (Exception e) {
                        log.error("Worker 'authenticate-user': unexpected error during authentication for '{}'", username, e);
                        externalTaskService.handleBpmnError(
                                externalTask,
                                "UNEXPECTED_ERROR",
                                "Произошла ошибка во время авторизации пользователя " + e.getMessage(),
                                Map.of("unexpectedErrorMessage", "Неожиданная ошибка: " + e.getMessage())
                        );
                    }
                })
                .open();
    }
}
