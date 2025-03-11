package com.greenheaven.greenheaven_app.exception;

public class OldPasswordMismatchException extends RuntimeException {
    public OldPasswordMismatchException(String message) {
        super(message);
    }
}
