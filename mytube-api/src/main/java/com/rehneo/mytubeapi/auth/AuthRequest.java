package com.rehneo.mytubeapi.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequest {
    @NotNull(message = "Username cannot be null")
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 4, max = 128, message = "Username must be between 4 and 128 characters long")
    private String username;

    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 6, max = 128, message = "Password must be between 6 and 128 characters long")
    private String password;
}