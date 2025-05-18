package com.rehneo.mytubeapi.dto;

import com.rehneo.mytubeapi.user.UserReadDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoUserReadDto {
    private Integer id;
    private String name;
    private String description;
    private ZonedDateTime createdAt;
    private UserReadDto user;
}

