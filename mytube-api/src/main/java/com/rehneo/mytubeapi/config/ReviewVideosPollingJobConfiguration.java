package com.rehneo.mytubeapi.config;

import com.rehneo.mytubeapi.job.ReviewVideosPollingJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReviewVideosPollingJobConfiguration {
    @Bean
    public JobDetail reviewVideosPollingJobDetail() {
        return JobBuilder.newJob(ReviewVideosPollingJob.class)
                .withIdentity("reviewVideosPollingJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger reviewVideosPollingJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(reviewVideosPollingJobDetail())
                .withIdentity("reviewVideosPollingJobTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(1)
                        .repeatForever())
                .build();
    }
}
