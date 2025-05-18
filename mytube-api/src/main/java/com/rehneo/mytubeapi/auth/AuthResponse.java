package com.rehneo.mytubeapi.auth;

import com.rehneo.mytubeapi.user.UserReadDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private UserReadDto user;
}
