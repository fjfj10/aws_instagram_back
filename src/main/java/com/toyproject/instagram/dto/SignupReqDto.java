package com.toyproject.instagram.dto;

import com.toyproject.instagram.entity.User;
import lombok.Data;

@Data
public class SignupReqDto {
    private String phoneAndEmail;
    private String name;
    private String username;
    private String password;

    public User toUserEntity() {
        return User.builder()
                .email(phoneAndEmail)
                .name(name)
                .username(username)
                .password(password)
                .build();
    }
}
