package com.rehneo.mytubeapi.job;

import com.rehneo.mytubeapi.domain.ModerationStatus;
import com.rehneo.mytubeapi.domain.Video;
import com.rehneo.mytubeapi.repository.VideoRepository;
import com.rehneo.mytubeapi.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewVideosPollingJob implements Job {
    private final VideoRepository videoRepository;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("Starting review videos polling job");
        ZonedDateTime threshold = ZonedDateTime.now().minusMinutes(15L);
        List<Video> videos = videoRepository.findAllByModerationStatusAndOlderThan(
                ModerationStatus.REVIEW,
                threshold
        );
        videos.forEach(video -> kafkaProducerService.sendVideoUploadedEvent(video.getId()));
        log.info("Finished review videos polling job");
    }
}
