package com.rehneo.mytubeapi.service;

import com.rehneo.mytubeapi.domain.Ban;
import com.rehneo.mytubeapi.domain.BanType;
import com.rehneo.mytubeapi.error.ConflictException;
import com.rehneo.mytubeapi.repository.BanRepository;
import com.rehneo.mytubeapi.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BanService {
    private final BanRepository repository;

    @Transactional
    public void banByStrikes(User user) {
        if (repository.existsByUser(user)) {
            throw new ConflictException("User is already banned");
        }
        repository.save(Ban.builder()
                .user(user)
                .type(BanType.STRIKES)
                .build()
        );
    }

    public boolean isBanned(User user) {
        return repository.existsByUser(user);
    }
}
