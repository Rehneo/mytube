package com.rehneo.mytubeapi.user;

import com.rehneo.mytubeapi.error.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
