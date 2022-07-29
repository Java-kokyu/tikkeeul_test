package com.hanghae0705.sbmoney.security.oauth;

import com.hanghae0705.sbmoney.model.domain.user.RefreshToken;
import com.hanghae0705.sbmoney.model.dto.TokenDto;
import com.hanghae0705.sbmoney.repository.user.RefreshTokenRepository;
import com.hanghae0705.sbmoney.security.auth.UserDetailsImpl;
import com.hanghae0705.sbmoney.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
//    private final AppProperties appProperties;
    //private final HttpCookieOAuth2AuthorizationRequestRepository cookieOAuth2AuthorizationResquestRepository;

//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
//        super.onAuthenticationSuccess(request, response, authentication);
//    }

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

            //UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            TokenDto tokenDto = tokenProvider.generateAccessToken(authentication);

            RefreshToken refreshToken = RefreshToken.builder()
                    .key(authentication.getName())
                    .value(tokenProvider.generateRefreshToken())
                    .build();

            refreshTokenRepository.save(refreshToken);

//            CookieUtils.deleteCookie(request, response, "refreshToken");
//            CookieUtils.addCookie(response, "refreshToken", refreshToken.getValue(), TokenProvider.JWT_REFRESH_TOKEN_VALID_MILLI_SEC);

    //        //redirectUri 에 대한 설정이 구현되어 있음
    //        super.onAuthenticationSuccess(request, response, chain, authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String targetUrl = UriComponentsBuilder.fromUriString("https://www.tikkeeul.com/oauth2/redirect")
                    .queryParam("Authorization", tokenDto.getAccessToken())
                    .queryParam("refreshToken", tokenDto.getRefreshToken())
                    .queryParam("username", userDetails.getUsername()) // email, nickname
                    .build().toUriString();
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }

}
