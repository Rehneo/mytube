package com.rehneo.mytubeapi.service;

import com.rehneo.mytubeapi.domain.ModerationStatus;
import com.rehneo.mytubeapi.domain.Video;
import com.rehneo.mytubeapi.domain.VideoStatus;
import com.rehneo.mytubeapi.dto.VideoAdminReadDto;
import com.rehneo.mytubeapi.dto.VideoUserReadDto;
import com.rehneo.mytubeapi.dto.VideoCreateDto;
import com.rehneo.mytubeapi.error.AccessDeniedException;
import com.rehneo.mytubeapi.error.BadRequestException;
import com.rehneo.mytubeapi.error.ResourceNotFoundException;
import com.rehneo.mytubeapi.error.ConflictException;
import com.rehneo.mytubeapi.repository.VideoRepository;
import com.rehneo.mytubeapi.repository.VideoStorage;
import com.rehneo.mytubeapi.user.User;
import com.rehneo.mytubeapi.user.Role;
import com.rehneo.mytubeapi.user.UserService;
import com.rehneo.mytubeapi.mapper.VideoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;
    private final VideoStorage videoStorage;
    private final TextValidationService validationService;
    private final KafkaProducerService kafkaProducerService;
    private final UserService userService;
    private final VideoMapper mapper;

    @Transactional
    public VideoUserReadDto save(VideoCreateDto createDto, MultipartFile file) throws Exception {
        User user = userService.getCurrentUser();
        if (userService.isBanned(user)) {
            throw new AccessDeniedException("User is banned");
        }

        if(validationService.containsForbiddenWord(createDto.getDescription().concat(createDto.getName()))){
            throw new BadRequestException("Input data contains forbidden words");
        }

        int nextId = videoRepository.getNextVideoId();
        videoStorage.save(nextId, file.getInputStream(), file.getSize());
        Video video = Video.builder()
                .id(nextId)
                .name(createDto.getName())
                .status(VideoStatus.ACTIVE)
                .user(user)
                .description(createDto.getDescription())
                .createdAt(ZonedDateTime.now())
                .moderationStatus(ModerationStatus.PENDING)
                .build();

        try {
            videoRepository.save(video);
        } catch (Exception e) {
            videoStorage.remove(nextId);
            throw e;
        }

        kafkaProducerService.sendVideoUploadedEvent(nextId);

        return mapper.mapForUser(video);
    }

    public byte[] getVideoFile(int id) throws Exception {
        Video video = videoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                "Video with id " + id + " not found"
        ));
        if (video.getStatus() == VideoStatus.DELETED) {
            throw new ResourceNotFoundException("Video with id " + id + " not found");
        }
        return videoStorage.find(video.getId());
    }

    public VideoUserReadDto getUserVideoMetadata(int id) {
        Video video = videoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                "Video with id " + id + " not found"
        ));
        return mapper.mapForUser(video);
    }

    public VideoAdminReadDto getAdminVideoMetadata(int id) {
        Video video = videoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                "Video with id " + id + " not found"
        ));
        return mapper.mapForAdmin(video);
    }

    @Transactional
    public void deleteByAdmin(int videoId, String reason) {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new ResourceNotFoundException(
                "Video with id " + videoId + " not found"
        ));
        if (video.getStatus() == VideoStatus.DELETED) {
            throw new ConflictException("Video file with id " + videoId + " is already deleted");
        }
        video.setStatus(VideoStatus.DELETED);
        video.setDeletedBy(Role.ADMIN);
        video.setDeletionReason(reason);
        video.setDeletedAt(ZonedDateTime.now());
        videoRepository.save(video);
    }
}
