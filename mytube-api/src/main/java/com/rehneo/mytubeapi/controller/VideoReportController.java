package com.rehneo.mytubeapi.controller;

import com.rehneo.mytubeapi.dto.VideoReportAdminReadDto;
import com.rehneo.mytubeapi.dto.VideoReportCreateDto;
import com.rehneo.mytubeapi.dto.VideoReportUserReadDto;
import com.rehneo.mytubeapi.service.VideoReportService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/videos/reports", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class VideoReportController {
    private final VideoReportService service;

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<VideoReportUserReadDto> report(@RequestBody VideoReportCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.report(dto));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<VideoReportAdminReadDto> reject(@PathVariable int id) {
        return ResponseEntity.ok(service.rejectReport(id));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<VideoReportAdminReadDto> approve(
            @PathVariable int id,
            @RequestBody @Valid @NotNull @NotBlank String reason
    ) {
        return ResponseEntity.ok(service.approveReport(id, reason));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<VideoReportAdminReadDto>> findAllPending(Pageable pageable) {
        Page<VideoReportAdminReadDto> reports = service.findAllPending(pageable);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(reports.getTotalElements()))
                .body(reports);
    }

    @GetMapping("/approved")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<VideoReportAdminReadDto>> findAllApproved(Pageable pageable) {
        Page<VideoReportAdminReadDto> reports = service.findAllApproved(pageable);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(reports.getTotalElements()))
                .body(reports);
    }

    @GetMapping("/rejected")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<VideoReportAdminReadDto>> findAllRejected(Pageable pageable) {
        Page<VideoReportAdminReadDto> reports = service.findAllRejected(pageable);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(reports.getTotalElements()))
                .body(reports);
    }
}
