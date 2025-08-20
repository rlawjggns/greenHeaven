package com.greenheaven.backend.exception;

public class OldPasswordMismatchException extends Exception {
    public OldPasswordMismatchException(String message) {
        super(message);
    }
}
