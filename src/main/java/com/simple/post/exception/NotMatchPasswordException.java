package com.simple.post.exception;

public class NotMatchPasswordException extends RuntimeException {

    public NotMatchPasswordException(String message) {
        super(message);
    }
}
