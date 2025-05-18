package com.rehneo.mytubeapi.dto;


import com.rehneo.mytubeapi.user.UserReadDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrikeReadDto {
    private Integer id;
    private UserReadDto user;
    private String reason;
    private Integer videoId;
}
