package com.greenheaven.greenheaven_app.exception;

public class NewPasswordBlankException extends RuntimeException {
    public NewPasswordBlankException(String message) {
        super(message);
    }
}
