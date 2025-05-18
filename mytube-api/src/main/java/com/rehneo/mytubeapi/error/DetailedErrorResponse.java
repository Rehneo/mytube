package com.rehneo.mytubeapi.error;

import lombok.Getter;

import java.util.List;

@Getter
public class DetailedErrorResponse extends ErrorResponse {
    private final List<String> errors;

    public DetailedErrorResponse(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
}