package com.greenheaven.backend.exception;

public class NewPasswordBlankException extends RuntimeException {
    public NewPasswordBlankException(String message) {
        super(message);
    }
}
