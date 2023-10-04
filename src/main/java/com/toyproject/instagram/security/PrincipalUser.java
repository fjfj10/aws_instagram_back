package com.toyproject.instagram.security;

import com.toyproject.instagram.entity.Authority;
import com.toyproject.instagram.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// 하나라도 false면 로그인 안됌
public class PrincipalUser implements UserDetails {

    private String phoneOrEmailOrUsername;
    private String password;
    private List<Authority> authorities;

    public PrincipalUser(String phoneOrEmailOrUsername, String password, List<Authority> authorities) {
        this.phoneOrEmailOrUsername = phoneOrEmailOrUsername;
        this.password = password;
        this.authorities = authorities;
    }

    // 로그인 할 사용자의 권한
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        authorities.forEach(authority ->
                authorityList.add(new SimpleGrantedAuthority(authority.getRole().getRoleName())));

        return authorityList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return phoneOrEmailOrUsername;
    }

    // 계정 만료(사용 기간이 정해진 계정)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금(비밀번호 5번 틀리면)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 계정 자격 증명 만료(공공인증서, 공동인증서, 휴대폰인증 만료)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 활성화(회원가입 후 본인인증이 아직 되지 않은 경우)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
