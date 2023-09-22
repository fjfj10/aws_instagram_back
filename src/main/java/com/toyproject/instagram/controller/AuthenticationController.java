package com.toyproject.instagram.controller;

import com.toyproject.instagram.dto.SignupReqDto;
import com.toyproject.instagram.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;

    @PostMapping("/user")
    public ResponseEntity<?> signup(@RequestBody SignupReqDto signupReqDto) {
        userService.signupUser(signupReqDto);
        return  ResponseEntity.ok(null);
    }
}
