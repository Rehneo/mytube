package com.rehneo.mytubeapi.repository;

import com.rehneo.mytubeapi.user.User;
import com.rehneo.mytubeapi.domain.Ban;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BanRepository extends JpaRepository<Ban, Integer> {
    boolean existsByUser(User user);
}
