package com.rehneo.mytubeapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StrikeCreateDto {
    @NotNull(message = "User ID cannot be null.")
    private Integer userId;

    @NotNull(message = "Reason cannot be null.")
    @NotBlank(message = "Reason cannot be blank.")
    private String reason;

    @NotNull(message = "Video ID cannot be null.")
    private Integer videoId;
}