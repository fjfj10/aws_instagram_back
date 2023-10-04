package com.toyproject.instagram.service;

import com.toyproject.instagram.dto.SigninReqDto;
import com.toyproject.instagram.dto.SignupReqDto;
import com.toyproject.instagram.entity.User;
import com.toyproject.instagram.exception.JwtException;
import com.toyproject.instagram.repository.UserMapper;
import com.toyproject.instagram.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public void signupUser(SignupReqDto signupReqDto) {
        User user = signupReqDto.toUserEntity(passwordEncoder);

        // 입력된 정보의 email과 phone 타입을 구분하여 DB에 저장
        String emailPattern = "^[a-zA-Z0-9]+@[0-9a-zA-Z]+\\.[a-z]*$";
        String phonePattern = "^[0-9]{11}+$";

        Pattern emailRegex = Pattern.compile(emailPattern);
        Pattern phoneRegex = Pattern.compile(phonePattern);

        Matcher emailMatcher = emailRegex.matcher(signupReqDto.getPhoneOrEmail());
        Matcher phoneMatcher = phoneRegex.matcher(signupReqDto.getPhoneOrEmail());

        if (emailMatcher.matches()) {
            user.setEmail(signupReqDto.getPhoneOrEmail());
        }
        if (phoneMatcher.matches()) {
            user.setPhone(signupReqDto.getPhoneOrEmail());
        }

        Integer executeCount = userMapper.saveUser(user);
    }

    public String signinUser(SigninReqDto signinReqDto) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(signinReqDto.getPhoneOrEmailOrUsername(), signinReqDto.getLoginPassword());

        // authenticationManagerBuilder는 UserDetailsService를 찾는다 -> PrincipalDetailsService로 가서 loadUserByUsername 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);

        return accessToken;
    }

    public Boolean authenticate(String token) {
        String accessToken = jwtTokenProvider.convertToken(token);
        if(!jwtTokenProvider.validateToken(accessToken)) {
            throw new JwtException("사용자 정보가 만료되었습니다. 다시 로그인 하세요.");
        }
        return true;
    }

}
