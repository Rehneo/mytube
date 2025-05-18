package com.rehneo.mytubeapi.controller;

import com.rehneo.mytubeapi.dto.VideoCreateDto;
import com.rehneo.mytubeapi.dto.VideoUserReadDto;
import com.rehneo.mytubeapi.error.BadFileExtensionError;
import com.rehneo.mytubeapi.error.FileIsEmptyError;
import com.rehneo.mytubeapi.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping(value = "/api/v1/videos", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class VideoController {
    private final VideoService service;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<VideoUserReadDto> upload(
            @RequestParam("video") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("description") String description
    ) throws Exception {
        System.out.println(file.getOriginalFilename() + file.getSize());
        if (file.isEmpty()) {
            throw new FileIsEmptyError("File not found");
        }
        var extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (extension == null || !Objects.equals(extension.toLowerCase(), "mp4")) {
            throw new BadFileExtensionError("File extension must be .mp4");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(
                new VideoCreateDto(name, description),
                file
        ));
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<byte[]> getVideoFile(@PathVariable int id) throws Exception {
        var fileContent = service.getVideoFile(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + id + ".mp4\"")
                .body(fileContent);
    }

    @GetMapping("/{id}/metadata")
    public ResponseEntity<VideoUserReadDto> getVideoMetadata(@PathVariable int id) {
        return ResponseEntity.ok(service.getUserVideoMetadata(id));
    }
}
