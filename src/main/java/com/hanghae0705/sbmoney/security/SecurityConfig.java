package com.hanghae0705.sbmoney.security;

import com.hanghae0705.sbmoney.security.auth.UserDetailsServiceImpl;
import com.hanghae0705.sbmoney.security.filter.LoginFilter;
import com.hanghae0705.sbmoney.security.handler.JwtAccessDeniedHandler;
import com.hanghae0705.sbmoney.security.handler.JwtAuthenticationEntryPoint;
import com.hanghae0705.sbmoney.security.jwt.JwtSecurityConfig;
import com.hanghae0705.sbmoney.security.jwt.TokenProvider;
import com.hanghae0705.sbmoney.security.oauth.OAuth2AuthenticationFailureHandler;
import com.hanghae0705.sbmoney.security.oauth.OAuth2AuthenticationSuccessHandler;
import com.hanghae0705.sbmoney.security.oauth.Oauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true) //@Secured 어노테이션 활성화 - admin 만 접근 제한할 수 있음
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private final UserDetailsServiceImpl userDetailsService;
    private final Oauth2UserService oauth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public SecurityConfig(TokenProvider tokenProvider, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtAccessDeniedHandler jwtAccessDeniedHandler, UserDetailsServiceImpl userDetailsService, Oauth2UserService oauth2UserService, OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler, OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.userDetailsService = userDetailsService;
        this.oauth2UserService = oauth2UserService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
    }

    @Bean
    public BCryptPasswordEncoder encodePassword(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        //http.cors().configurationSource(corsConfigurationSource());
        //http.addFilterBefore(formLoginFilter(), UsernamePasswordAuthenticationFilter.class);

        http.csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                .accessDeniedHandler(jwtAccessDeniedHandler)

                //h2 대시보드 허용
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                //세션 사용 금지
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                //접근권한 제어
                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated()
                //.anyRequest().permitAll()

                //토큰 프로바이더 사용
                .and()
                .apply(new JwtSecurityConfig(tokenProvider))

                //Oauth2
                .and()
                .oauth2Login()
                .loginPage("/user/login")
                //.and()
                .userInfoEndpoint() // 로그인 성공 후 사용자 정보 가져올 때의 설정
                .userService(oauth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler);
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        configuration.addAllowedOrigin("http://localhost:3000");
//        configuration.addAllowedOrigin("http://sparta-ej.shop");
//        configuration.addAllowedMethod(HttpMethod.POST);
//        configuration.addAllowedMethod(HttpMethod.GET);
//        configuration.addAllowedMethod(HttpMethod.POST);
//        configuration.addAllowedMethod(HttpMethod.PUT);
//        configuration.addAllowedMethod(HttpMethod.OPTIONS);
//        configuration.addAllowedMethod(HttpMethod.DELETE);
//        configuration.addAllowedHeader("Content-Type, Authorization");
//        //configuration.addAllowedOriginPattern("http://localhost:3000");
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

    private static final String[] AUTH_WHITELIST = {
            "/",
            "/h2-console/**",
            "/api/user/**",
            "/test",
            "/oauth2/**",
            "/login/oauth2/code/**",
            "/user/login",
            "/user/changePassword/**",
            "/api/board",
            "/api/board/detail/**",
            "/api/board/save/**",
            "/oauth2/redirect/**",
            "/login/oauth2/**",
            "/api/statistics/**",
            "/chatting/**"
            //"/api/chat/**"
            //"/api/goalItem"
    };

}
