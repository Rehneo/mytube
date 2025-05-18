package com.rehneo.mytubeapi.mapper;

import com.rehneo.mytubeapi.user.UserMapper;
import com.rehneo.mytubeapi.domain.Strike;
import com.rehneo.mytubeapi.dto.StrikeReadDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StrikeMapper {
    private final UserMapper userMapper;

    public StrikeReadDto map(Strike strike) {
        return StrikeReadDto.builder()
                .id(strike.getId())
                .videoId(strike.getVideo().getId())
                .user(userMapper.map(strike.getUser()))
                .reason(strike.getReason())
                .build();
    }
}
