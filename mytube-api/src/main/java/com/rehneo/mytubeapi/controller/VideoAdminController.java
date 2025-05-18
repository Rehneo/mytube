package com.rehneo.mytubeapi.controller;


import com.rehneo.mytubeapi.dto.VideoAdminReadDto;
import com.rehneo.mytubeapi.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/admin/videos", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class VideoAdminController {
    private final VideoService service;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}/metadata")
    public ResponseEntity<VideoAdminReadDto> getVideoMetadata(@PathVariable int id) {
        return ResponseEntity.ok(service.getAdminVideoMetadata(id));
    }
}
