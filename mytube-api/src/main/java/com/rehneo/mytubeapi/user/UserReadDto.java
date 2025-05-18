package com.rehneo.mytubeapi.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserReadDto {
    private Integer id;
    private String username;
    private Role role;
}
