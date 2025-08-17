package com.greenheaven.backend.exception;

public class NewPasswordMismatchException extends RuntimeException {
    public NewPasswordMismatchException(String message) {
        super(message);
    }
}
