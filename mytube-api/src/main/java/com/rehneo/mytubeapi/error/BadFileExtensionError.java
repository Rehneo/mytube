package com.rehneo.mytubeapi.error;

public class BadFileExtensionError extends BadRequestException {
    public BadFileExtensionError(String message) {
        super(message);
    }
}
