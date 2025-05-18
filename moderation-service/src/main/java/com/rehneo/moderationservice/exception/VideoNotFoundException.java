package com.rehneo.moderationservice.exception;

public class VideoNotFoundException extends RuntimeException {
    public VideoNotFoundException(Integer videoId) {
        super("Video with id: " + videoId + " not found");
    }
}
