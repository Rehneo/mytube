package com.rehneo.mytubeapi.worker;

import com.rehneo.mytubeapi.security.JwtService;
import com.rehneo.mytubeapi.user.User;
import com.rehneo.mytubeapi.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthorizeUserWorker {
    private final ExternalTaskClient client;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @PostConstruct
    public void subscribe() {
        client.subscribe("authorize-user")
                .lockDuration(10000)
                .handler((externalTask, externalTaskService) -> {
                    String token = externalTask.getVariable("jwt");
                    String requiredRole = externalTask.getVariable("requiredRole");
                    String errorMessage;
                    log.info("Worker 'authorize-user': processing token validation for role '{}'", requiredRole);

                    try {
                        String username = jwtService.extractUsername(token);
                        var userDetails = userDetailsService.loadUserByUsername(username);

                        if (!jwtService.isTokenValid(token, userDetails)) {
                            log.warn("Worker 'authorize-user': invalid token for user '{}'", username);
                            errorMessage = "Неверный токен";
                            externalTaskService.handleBpmnError(
                                    externalTask,
                                    "AUTH_FAILED",
                                    errorMessage,
                                    Map.of("authErrorMessage", errorMessage)
                            );
                            return;
                        }

                        User user = userService.getByUsername(username);
                        if (userService.isBanned(user)) {
                            log.warn("Worker 'authorize-user': user {} is banned", username);
                            errorMessage = "Пользователь с логином: " + username + " забанен";
                            externalTaskService.handleBpmnError(
                                    externalTask,
                                    "AUTH_FAILED",
                                    errorMessage,
                                    Map.of("authErrorMessage", errorMessage)
                            );
                        }

                        boolean hasRequiredRole = userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .anyMatch(authority -> authority.equals(requiredRole));

                        if (!hasRequiredRole) {
                            log.warn("Worker 'authorize-user': user '{}' does not have required role '{}'", username, requiredRole);
                            errorMessage = "У пользователя нет требуемой роли: " + requiredRole;
                            externalTaskService.handleBpmnError(
                                    externalTask,
                                    "AUTH_FAILED",
                                    errorMessage,
                                    Map.of("authErrorMessage", errorMessage)
                            );
                            return;
                        }

                        log.info("Worker 'authorize-user': token validation successful for user '{}' with role '{}'", username, requiredRole);
                        externalTaskService.complete(externalTask, Map.of("username", username));

                    } catch (Exception e) {
                        log.error("Worker 'authorize-user': error during token validation");
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
