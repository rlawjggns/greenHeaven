package com.greenheaven.backend.exception;

public class NewPasswordLengthException extends RuntimeException {
    public NewPasswordLengthException(String message) {
        super(message);
    }
}
