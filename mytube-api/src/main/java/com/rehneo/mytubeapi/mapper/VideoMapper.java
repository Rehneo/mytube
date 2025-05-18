package com.rehneo.mytubeapi.mapper;

import com.rehneo.mytubeapi.domain.Video;
import com.rehneo.mytubeapi.dto.VideoAdminReadDto;
import com.rehneo.mytubeapi.dto.VideoUserReadDto;
import com.rehneo.mytubeapi.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VideoMapper {
    private final UserMapper userMapper;

    public VideoUserReadDto mapForUser(Video video) {
        return VideoUserReadDto.builder()
                .id(video.getId())
                .description(video.getDescription())
                .createdAt(video.getCreatedAt())
                .name(video.getName())
                .user(userMapper.map(video.getUser()))
                .build();
    }

    public VideoAdminReadDto mapForAdmin(Video video) {
        return VideoAdminReadDto.builder()
                .id(video.getId())
                .description(video.getDescription())
                .createdAt(video.getCreatedAt())
                .name(video.getName())
                .user(userMapper.map(video.getUser()))
                .status(video.getStatus())
                .deletionReason(video.getDeletionReason())
                .deletedAt(video.getDeletedAt())
                .deletedBy(video.getDeletedBy())
                .build();
    }
}
