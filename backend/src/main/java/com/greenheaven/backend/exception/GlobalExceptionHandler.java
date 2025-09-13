package com.greenheaven.backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            OldPasswordMismatchException.class,
    })
    public ResponseEntity<Map<String, String>> handleProfileUpdateExceptions(Exception ex) {
        Map<String, String> errors = new HashMap<>();

        // 예외 타입에 따라 key를 정해주기
        if (ex instanceof OldPasswordMismatchException) errors.put("oldPassword", ex.getMessage());

        return ResponseEntity.badRequest().body(errors);
    }
}
