package com.rehneo.mytubeapi.service;

import com.rehneo.mytubeapi.domain.ModerationStatus;
import com.rehneo.mytubeapi.domain.Video;
import com.rehneo.mytubeapi.domain.VideoStatus;
import com.rehneo.mytubeapi.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RejectedVideosDeleteService {
    private final VideoRepository videoRepository;

    @Transactional
    public void execute() {
        List<Video> rejectedVideos = videoRepository.findAllByModerationStatus(ModerationStatus.REJECTED);
        rejectedVideos.forEach(video -> video.setStatus(VideoStatus.DELETED));
    }
}
