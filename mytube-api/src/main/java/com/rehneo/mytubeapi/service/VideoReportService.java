package com.rehneo.mytubeapi.service;

import com.rehneo.mytubeapi.domain.Video;
import com.rehneo.mytubeapi.domain.VideoReport;
import com.rehneo.mytubeapi.domain.VideoReportStatus;
import com.rehneo.mytubeapi.domain.VideoStatus;
import com.rehneo.mytubeapi.dto.VideoReportAdminReadDto;
import com.rehneo.mytubeapi.dto.VideoReportCreateDto;
import com.rehneo.mytubeapi.dto.VideoReportUserReadDto;
import com.rehneo.mytubeapi.error.ConflictException;
import com.rehneo.mytubeapi.error.ResourceNotFoundException;
import com.rehneo.mytubeapi.mapper.VideoReportMapper;
import com.rehneo.mytubeapi.repository.VideoReportRepository;
import com.rehneo.mytubeapi.repository.VideoRepository;
import com.rehneo.mytubeapi.user.User;
import com.rehneo.mytubeapi.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class VideoReportService {
    private final VideoRepository videoRepository;
    private final VideoReportMapper mapper;
    private final VideoReportRepository repository;
    private final UserService userService;
    private final VideoService videoService;

    @Transactional
    public VideoReportUserReadDto report(VideoReportCreateDto dto) {
        Video video = videoRepository.findById(dto.getVideoId()).orElseThrow(
                () -> new ResourceNotFoundException("Video with id " + dto.getVideoId() + " not found")
        );
        if(video.getStatus() == VideoStatus.DELETED){
            throw new ConflictException("Video with id " + dto.getVideoId() + " is deleted");
        }
        User user = userService.getCurrentUser();
        VideoReport videoReport = VideoReport.builder()
                .description(dto.getDescription())
                .sender(user)
                .status(VideoReportStatus.PENDING)
                .video(video)
                .build();
        repository.save(videoReport);
        return mapper.mapForUser(videoReport);
    }

    @Transactional
    public VideoReportAdminReadDto rejectReport(int reportId) {
        VideoReport report = repository.findById(reportId).orElseThrow(
                () -> new ResourceNotFoundException("Video report with id " + reportId + " not found")
        );
        if(report.getStatus() != VideoReportStatus.PENDING){
            throw new ConflictException("report with id " + reportId + " has already been processed");
        }
        report.setStatus(VideoReportStatus.REJECTED);
        report.setProcessedAt(ZonedDateTime.now());
        repository.save(report);
        return mapper.mapForAdmin(report);
    }

    @Transactional
    public VideoReportAdminReadDto approveReport(int reportId, String reason) {
        VideoReport report = repository.findById(reportId).orElseThrow(
                () -> new ResourceNotFoundException("Video report with id " + reportId + " not found")
        );
        if(report.getStatus() != VideoReportStatus.PENDING){
            throw new ConflictException("report with id " + reportId + " has already been processed");
        }
        report.setStatus(VideoReportStatus.APPROVED);
        report.setProcessedAt(ZonedDateTime.now());
        repository.save(report);
        videoService.deleteByAdmin(report.getVideo().getId(), reason);
        return mapper.mapForAdmin(report);
    }

    public Page<VideoReportAdminReadDto> findAllPending(Pageable pageable) {
        return repository.findAllByStatusOrderByCreatedAtDesc(
                VideoReportStatus.PENDING, pageable
        ).map(mapper::mapForAdmin);
    }

    public Page<VideoReportAdminReadDto> findAllApproved(Pageable pageable) {
        return repository.findAllByStatusOrderByCreatedAtDesc(
                VideoReportStatus.APPROVED, pageable
        ).map(mapper::mapForAdmin);
    }

    public Page<VideoReportAdminReadDto> findAllRejected(Pageable pageable) {
        return repository.findAllByStatusOrderByCreatedAtDesc(
                VideoReportStatus.REJECTED, pageable
        ).map(mapper::mapForAdmin);
    }
}
