package com.rehneo.mytubeapi.dto;

import com.rehneo.mytubeapi.domain.VideoStatus;
import com.rehneo.mytubeapi.user.Role;
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
public class VideoAdminReadDto {
    private Integer id;
    private String name;
    private String description;
    private ZonedDateTime createdAt;
    private ZonedDateTime deletedAt;
    private VideoStatus status;
    private String deletionReason;
    private Role deletedBy;
    private UserReadDto user;
}
