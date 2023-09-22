package com.toyproject.instagram.service;

import com.toyproject.instagram.dto.SignupReqDto;
import com.toyproject.instagram.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public void signupUser(SignupReqDto signupReqDto) {
        Integer executeCount = userMapper.saveUser(signupReqDto.toUserEntity());
        System.out.println(executeCount);
    }

}
