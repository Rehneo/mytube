package com.rehneo.mytubeapi.config;

import com.rehneo.mytubeapi.job.RejectedVideosDeleteJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RejectedVideosDeleteJobConfiguration {
    @Bean
    public JobDetail rejectedVideosDeleteJobDetail() {
        return JobBuilder.newJob(RejectedVideosDeleteJob.class)
                .withIdentity("rejectedVideosDeleteJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger rejectedVideosDeleteJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(rejectedVideosDeleteJobDetail())
                .withIdentity("rejectedVideosDeleteTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(2)
                        .repeatForever())
                .build();
    }
}
