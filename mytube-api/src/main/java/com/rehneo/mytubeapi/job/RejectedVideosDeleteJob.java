package com.rehneo.mytubeapi.job;

import com.rehneo.mytubeapi.service.RejectedVideosDeleteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RejectedVideosDeleteJob implements Job {
    private final RejectedVideosDeleteService rejectedVideosDeleteService;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("Starting rejected videos cleanup job");
        rejectedVideosDeleteService.execute();
        log.info("Finished rejected videos cleanup job");
    }
}
