package com.toyproject.instagram.controller;

import com.toyproject.instagram.exception.JwtException;
import com.toyproject.instagram.exception.SignupException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
    // 예외가 생기는 순간 실행, 스프링에서의 예외처리 -> 예외가 생겼다고 프로그램이 중단되면X -> 예외에 대한 응답을 준다
    // ExceptionHandler는 SignupException클래스가 생기길 기다렸다가 생기는 순간 예외를 받는다
    @ExceptionHandler(SignupException.class)
    public ResponseEntity<?> signupExceptionHandle(SignupException signupException) {
        return ResponseEntity.badRequest().body(signupException.getErrorMap());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> jwtExceptionHandle(JwtException jwtException) {
        return ResponseEntity.badRequest().body(jwtException.getMessage());
    }
}
