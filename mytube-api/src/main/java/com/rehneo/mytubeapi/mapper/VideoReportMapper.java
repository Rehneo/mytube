package com.rehneo.mytubeapi.mapper;

import com.rehneo.mytubeapi.domain.VideoReport;
import com.rehneo.mytubeapi.dto.VideoReportAdminReadDto;
import com.rehneo.mytubeapi.dto.VideoReportUserReadDto;
import com.rehneo.mytubeapi.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VideoReportMapper {
    private final UserMapper userMapper;
    private final VideoMapper videoMapper;

    public VideoReportUserReadDto mapForUser(VideoReport videoReport) {
        return VideoReportUserReadDto.builder()
                .id(videoReport.getId())
                .sender(userMapper.map(videoReport.getSender()))
                .createdAt(videoReport.getCreatedAt())
                .processedAt(videoReport.getProcessedAt())
                .channel(videoMapper.mapForUser(videoReport.getVideo()))
                .description(videoReport.getDescription())
                .status(videoReport.getStatus())
                .build();
    }

    public VideoReportAdminReadDto mapForAdmin(VideoReport videoReport) {
        return VideoReportAdminReadDto.builder()
                .id(videoReport.getId())
                .sender(userMapper.map(videoReport.getSender()))
                .createdAt(videoReport.getCreatedAt())
                .processedAt(videoReport.getProcessedAt())
                .channel(videoMapper.mapForAdmin(videoReport.getVideo()))
                .description(videoReport.getDescription())
                .status(videoReport.getStatus())
                .build();
    }
}
