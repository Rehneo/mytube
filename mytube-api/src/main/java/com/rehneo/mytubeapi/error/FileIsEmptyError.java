package com.rehneo.mytubeapi.error;

public class FileIsEmptyError extends BadRequestException {
    public FileIsEmptyError(String message) {
        super(message);
    }
}
