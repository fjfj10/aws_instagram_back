package com.toyproject.instagram.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity  // 현재 우리가 만든 Security 설정 정책을 따른다
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean   // 라이브러리에서 들고온 컴포넌트 IoC등록
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();    // WebMvcConfig에서 설정한 cors 정책을 따르겠다
        http.csrf().disable();  // csrf 토큰 비활성화

        http.authorizeRequests()
                .antMatchers("/api/v1/auth/**") // /api/v1/auth/로 시작하는 모든 요청
                .permitAll();   // 인증없이 요청을 허용
    }
}