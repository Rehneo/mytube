package com.rehneo.mytubeapi.controller;


import com.rehneo.mytubeapi.dto.StrikeCreateDto;
import com.rehneo.mytubeapi.dto.StrikeReadDto;
import com.rehneo.mytubeapi.service.StrikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/strikes", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class StrikeController {
    private final StrikeService strikeService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<StrikeReadDto> strike(@RequestBody StrikeCreateDto strikeCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(strikeService.giveStrike(strikeCreateDto));
    }
}
