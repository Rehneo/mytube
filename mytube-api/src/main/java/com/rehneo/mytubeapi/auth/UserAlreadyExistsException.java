package com.rehneo.mytubeapi.auth;

import com.rehneo.mytubeapi.error.ConflictException;

public class UserAlreadyExistsException extends ConflictException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
