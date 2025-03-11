package com.greenheaven.greenheaven_app.exception;

public class NewPasswordMismatchException extends RuntimeException {
    public NewPasswordMismatchException(String message) {
        super(message);
    }
}
