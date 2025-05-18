package com.rehneo.moderationservice.exception;

public class IllegalModerationStatusException extends RuntimeException{
    public IllegalModerationStatusException(String message) {
        super(message);
    }
}
