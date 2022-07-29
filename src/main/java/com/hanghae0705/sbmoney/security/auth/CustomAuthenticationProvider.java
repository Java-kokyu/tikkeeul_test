package com.hanghae0705.sbmoney.security.auth;

import com.hanghae0705.sbmoney.exception.ApiException;
import com.hanghae0705.sbmoney.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        System.out.println(password);


        UserDetails userDetails = (UserDetails) userDetailsService.loadUserByUsername(username);

        String signPassword = userDetails.getPassword();
        System.out.println(signPassword);
        if(!passwordEncoder.matches(password, signPassword)) {			//ID,PW 틀린 경우 OR 계정이 없는 경우
            throw new ApiRequestException(ApiException.NOT_VALID_DATA);
        } else if(!userDetails.isAccountNonLocked()) {			//계정이 잠긴 경우(true로 고정, 추후 옵션처리 가능)
            throw new LockedException(username);
        } else if(!userDetails.isEnabled()) {					//계정이 비활성화된 경우
            throw new DisabledException(username);
        } else if(!userDetails.isAccountNonExpired()) {			//계정이 만료된 경우
            throw new AccountExpiredException(username);
        } else if(!userDetails.isCredentialsNonExpired()) {		//비밀번호가 만료된 경우
            throw new CredentialsExpiredException(username);
        }

        Authentication newAuth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        return newAuth;
    }

    // 토큰 타입과 일치할 때 인증
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}