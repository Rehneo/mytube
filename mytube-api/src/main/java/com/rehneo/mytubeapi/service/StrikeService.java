package com.rehneo.mytubeapi.service;

import com.rehneo.mytubeapi.domain.Strike;
import com.rehneo.mytubeapi.domain.Video;
import com.rehneo.mytubeapi.dto.StrikeCreateDto;
import com.rehneo.mytubeapi.dto.StrikeReadDto;
import com.rehneo.mytubeapi.error.ConflictException;
import com.rehneo.mytubeapi.error.ResourceNotFoundException;
import com.rehneo.mytubeapi.mapper.StrikeMapper;
import com.rehneo.mytubeapi.repository.StrikeRepository;
import com.rehneo.mytubeapi.repository.VideoRepository;
import com.rehneo.mytubeapi.user.User;
import com.rehneo.mytubeapi.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StrikeService {
    private final StrikeRepository repository;
    private final VideoRepository videoRepository;
    private final UserService userService;
    private final StrikeMapper mapper;

    @Transactional
    public StrikeReadDto giveStrike(StrikeCreateDto strikeCreateDto) {
        User user = userService.getById(strikeCreateDto.getUserId());
        if (userService.isBanned(user)) {
            throw new ConflictException("User is already banned");
        }
        int numberOfStrikes = repository.getNumberOfStrikesByUserId(user.getId());
        if (numberOfStrikes >= 2) {
            userService.banByStrikes(user);
        }
        Video video = videoRepository.findById(strikeCreateDto.getVideoId()).orElseThrow(
                () -> new ResourceNotFoundException("Video with id " + strikeCreateDto.getVideoId() + " not found")
        );
        Strike strike = Strike.builder()
                .video(video)
                .user(user)
                .reason(strikeCreateDto.getReason())
                .build();
        repository.save(strike);
        return mapper.map(strike);
    }
}
