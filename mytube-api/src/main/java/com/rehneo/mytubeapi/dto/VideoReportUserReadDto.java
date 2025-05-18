package com.rehneo.mytubeapi.dto;

import com.rehneo.mytubeapi.domain.VideoReportStatus;
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
public class VideoReportUserReadDto {
    private Integer id;
    private String description;
    private UserReadDto sender;
    private VideoReportStatus status;
    private ZonedDateTime createdAt;
    private VideoUserReadDto channel;
    private ZonedDateTime processedAt;
}
