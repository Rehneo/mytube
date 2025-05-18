package com.rehneo.mytubeapi.repository;

import com.rehneo.mytubeapi.domain.ModerationStatus;
import com.rehneo.mytubeapi.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {

    @Query(value = "SELECT nextval('videos_id_seq')", nativeQuery = true)
    int getNextVideoId();

    List<Video> findAllByModerationStatus(ModerationStatus status);

    @Query("SELECT v FROM Video v WHERE v.moderationStatus = :status AND v.createdAt < :threshold")
    List<Video> findAllByModerationStatusAndOlderThan(ModerationStatus status, ZonedDateTime threshold);
}
