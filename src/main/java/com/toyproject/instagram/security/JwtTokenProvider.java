package com.toyproject.instagram.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

// JWT 토큰을 생성 해주는 로직
@Component
public class JwtTokenProvider {

    private final Key key;

    // Autowired는 IoC 컨테이너에서 객체를 자동 주입
    // Value는 application.yml에서 변수 데이터 자동 주입

    // IoC에서 생성 시 yml에서 jwt.secret을 가지고 온다
    public JwtTokenProvider(@Value("${jwt.secret}")String secret) {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    // JWT 토큰을 생성하는 로직
    public String generateAccessToken(Authentication authentication) {
        String accessToken = null;

        // 로그인된 UserDetails을 가지고옴
//        System.out.println(authentication.getPrincipal().getClass());
        // PrincipalUser로 다운캐스팅 가능
        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal();

        // (1000 * 60 * 60 * 24) 24시간동안 유효한 Token
        Date tokenExpiresDate = new Date(new Date().getTime() + (1000 * 60 * 60 * 24));

        accessToken = Jwts.builder()
                .setSubject("AccessToken")
                .claim("username", principalUser.getUsername())
                .setExpiration(tokenExpiresDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return accessToken;
    }

    // 토큰 유효성 검사
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    public String convertToken(String bearerToken) {
        String type = "Bearer ";
        //StringUtils : null 확인, 공백 확인을 동시에 해줌
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(type)) {
            return bearerToken.substring(type.length());
        }
        return "";
    }

}
