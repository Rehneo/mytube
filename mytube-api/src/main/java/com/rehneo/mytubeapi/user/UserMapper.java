package com.rehneo.mytubeapi.user;

import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public UserReadDto map(User user) {
        return UserReadDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
