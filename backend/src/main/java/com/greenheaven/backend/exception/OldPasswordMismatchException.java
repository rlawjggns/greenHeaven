package com.greenheaven.backend.exception;

public class OldPasswordMismatchException extends RuntimeException {
    public OldPasswordMismatchException(String message) {
        super(message);
    }
}
