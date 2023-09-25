package com.toyproject.instagram.dto;

import com.toyproject.instagram.entity.User;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignupReqDto {
//    @NotBlank(message = "전화번호 또는 이메일은 공백일 수 없습니다.")
    // regexp = "정규식 \\은 \를 의미: 이스케이프문자가 문자열안에 있기때문에, |=or, \\d = [0-9]"
    @Pattern(regexp = "^[a-zA-Z0-9]+@[0-9a-zA-Z]+\\.[a-z]*$|^[0-9]{11}+$", message = "이메일 또는 전화번호를 입력하세요.")
    private String phoneAndEmail;

//    @NotBlank(message = "이름은 공백일 수 없습니다.")
    @Pattern(regexp = "^[가-힣]{2,6}$", message = "한글만 입력할 수 있습니다.")
    private String name;

    @Pattern(regexp = "^(?=.*[a-z])[a-z0-9_.]*$", message = "사용할 수 없는 사용자 이름입니다. 다른 이름을 사용하세요.")
    private String username;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,25}$", message = "비밀번호는 영문,숫자 조합으로 8자 이상 입력하세요.")
    private String password;

    public User toUserEntity(BCryptPasswordEncoder passwordEncoder) {
        return User.builder()
                .email(phoneAndEmail)
                .name(name)
                .username(username)
                .password(passwordEncoder.encode(password)) // password 암호화
                .build();
    }
}
