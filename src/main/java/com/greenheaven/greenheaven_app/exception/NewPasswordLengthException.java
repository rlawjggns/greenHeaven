package com.greenheaven.greenheaven_app.exception;

public class NewPasswordLengthException extends RuntimeException {
    public NewPasswordLengthException(String message) {
        super(message);
    }
}
