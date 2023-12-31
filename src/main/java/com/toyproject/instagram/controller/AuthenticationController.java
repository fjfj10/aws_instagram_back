package com.toyproject.instagram.controller;

import com.toyproject.instagram.dto.SigninReqDto;
import com.toyproject.instagram.dto.SignupReqDto;
import com.toyproject.instagram.exception.SignupException;
import com.toyproject.instagram.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;

    @PostMapping("/user")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupReqDto signupReqDto, BindingResult bindingResult) {

        // 오류가 있을때만 작동, getFieldErrors():FieldError를 list에 담아꺼내줌
        if(bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errorMap.put(error.getField(), error.getDefaultMessage());
            });
            // 예외를 바로 처리하지 않고 SignupException으로 던짐
            throw new SignupException(errorMap);
        }

        userService.signupUser(signupReqDto);
        return  ResponseEntity.ok(null);
    }

    @PostMapping("/login")
    public ResponseEntity<?> signin(@RequestBody SigninReqDto signinReqDto) {

        String accessToken = userService.signinUser(signinReqDto);
        // 정상적으로 로그인이 되면 200으로 accessToken을 넘겨줌
        return ResponseEntity.ok().body(accessToken);
    }

    @GetMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(userService.authenticate(token));
    }

}
