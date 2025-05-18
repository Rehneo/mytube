package com.rehneo.moderationservice.service;

import com.rehneo.moderationservice.domain.ModerationStatus;
import com.rehneo.moderationservice.domain.Video;
import com.rehneo.moderationservice.exception.IllegalModerationStatusException;
import com.rehneo.moderationservice.exception.NexaraConnectionException;
import com.rehneo.moderationservice.exception.VideoNotFoundException;
import com.rehneo.moderationservice.exception.VideoStorageException;
import com.rehneo.moderationservice.repository.VideoRepository;
import com.rehneo.moderationservice.repository.VideoStorage;
import com.rehneo.nexara.api.ra.NexaraApiConnection;
import com.rehneo.nexara.api.ra.impl.NexaraApiConnectionImpl;
import jakarta.resource.ResourceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoModerationService {
    private final VideoRepository videoRepository;
    private final VideoStorage videoStorage;
    private final TextValidationService textValidationService;

    @Value("${nexara.api.key}")
    private String nexaraApiKey;

    @Transactional
    public void processModeration(Integer videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new VideoNotFoundException(videoId));

        if (video.getModerationStatus() != ModerationStatus.PENDING) {
            throw new IllegalModerationStatusException(
                    "Video moderation status must be PENDING but was " + video.getModerationStatus() + " " +
                            "in video with id: " + videoId
            );
        }

        byte[] videoData;
        try {
            videoData = videoStorage.find(videoId);
        } catch (Exception e) {
            throw new VideoStorageException("Failed to load video with id: " + videoId);
        }

        String text;
        NexaraApiConnection connection = null;
        try {
            connection = getConnection();
            text = connection.transcribe(videoData, nexaraApiKey);
        } catch (ResourceException e) {
            throw new NexaraConnectionException("ResourceException during direct connection usage: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (ResourceException ignored) {
                }
            }
        }

        log.info("transcribed video with id: {}, text: {}", videoId, text);

        if (textValidationService.containsForbiddenWord(text)) {
            video.setModerationStatus(ModerationStatus.REJECTED);
        } else {
            video.setModerationStatus(ModerationStatus.PASSED);
        }
        videoRepository.save(video);
    }

    private NexaraApiConnection getConnection() {
        return new NexaraApiConnectionImpl(null);
    }
}
