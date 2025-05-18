package com.rehneo.mytubeapi.repository;

import com.rehneo.mytubeapi.domain.Strike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StrikeRepository extends JpaRepository<Strike, Integer> {

    @Query("SELECT COUNT(s) FROM Strike s WHERE s.user.id = :userId")
    int getNumberOfStrikesByUserId(int userId);
}
