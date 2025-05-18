package com.rehneo.mytubeapi.repository;

import com.rehneo.mytubeapi.domain.VideoReport;
import com.rehneo.mytubeapi.domain.VideoReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoReportRepository extends JpaRepository<VideoReport, Integer> {

    Page<VideoReport> findAllByStatusOrderByCreatedAtDesc(VideoReportStatus status, Pageable pageable);

}
