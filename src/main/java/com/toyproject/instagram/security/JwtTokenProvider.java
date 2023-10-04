package com.toyproject.instagram.security;

import com.toyproject.instagram.entity.User;
import com.toyproject.instagram.repository.UserMapper;
import com.toyproject.instagram.service.PrincipalDetailsService;
import com.toyproject.instagram.service.UserService;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

// JWT 토큰을 관리해주는 로직
@Component
public class JwtTokenProvider {

    private final Key key;
    private final PrincipalDetailsService principalDetailsService;
    private final UserMapper userMapper;

    // Autowired는 IoC 컨테이너에서 객체를 자동 주입
    // Value는 application.yml에서 변수 데이터 자동 주입

    // IoC에서 생성 시 yml에서 jwt.secret을 가지고 온다
    public JwtTokenProvider(@Value("${jwt.secret}")String secret,
                            @Autowired PrincipalDetailsService principalDetailsService,
                            @Autowired UserMapper userMapper) {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.principalDetailsService = principalDetailsService;
        this.userMapper = userMapper;
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

        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject("AccessToken")
                .claim("username", principalUser.getUsername())
                .setExpiration(tokenExpiresDate)
                .signWith(key, SignatureAlgorithm.HS256);

        User user = userMapper.findUserByPhone(principalUser.getUsername());

        if(user != null) {
            return jwtBuilder.claim("username", user.getUsername()).compact();
        }

        user = userMapper.findUserByEmail(principalUser.getUsername());
        if(user != null) {
            return jwtBuilder.claim("username", user.getUsername()).compact();
        }

        return jwtBuilder.claim("username", user.getUsername()).compact();
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

    public Authentication getAuthentication(String accessToken) {
        Authentication authentication = null;
        // 암호화 해제 후 body를 가지고와 username을 키값으로 가진 값을 가지고온다
        String username = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .get("username")
                .toString();

        PrincipalUser principalUser = (PrincipalUser) principalDetailsService.loadUserByUsername(username);
        // 유저, 서명(생략가능), 권한을 가진 authentication 객체 생성
        authentication = new UsernamePasswordAuthenticationToken(principalUser, null, principalUser.getAuthorities());
        return authentication;
    }

}
