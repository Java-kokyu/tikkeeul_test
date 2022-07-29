package com.hanghae0705.sbmoney.security.auth;

import com.hanghae0705.sbmoney.model.domain.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class UserDetailsImpl implements UserDetails, OAuth2User {

    private final User user;
    private Map<String, Object> attributes;
    public UserDetailsImpl(User user) {
        this.user = user;
    }
    public User getUser(){
        return user;
    }

    //OAuth 로그인
    public UserDetailsImpl(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    //OAuth 의 정보가 Map에 담겨온다
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    //OAuth 오버라이드
    @Override
    public String getName() {
        return null;
    }

    //권한 받아오기
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(user.getRole().getAuthority());
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(simpleGrantedAuthority);

        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
