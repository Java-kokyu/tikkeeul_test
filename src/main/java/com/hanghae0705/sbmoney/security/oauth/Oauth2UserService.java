package com.hanghae0705.sbmoney.security.oauth;

import com.hanghae0705.sbmoney.model.domain.user.User;
import com.hanghae0705.sbmoney.model.domain.baseEntity.UserRoleEnum;
import com.hanghae0705.sbmoney.repository.user.UserRepository;
import com.hanghae0705.sbmoney.security.auth.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Oauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    //구글로 받은 userRequest 데이터에 대한 후처리 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest: "+userRequest);
        System.out.println("userRequest.getClientRegistration(): " + userRequest.getClientRegistration());

        // 로그인 토큰 값
        // 구글 로그인 버튼 클릭 -> 로그인완료 -> code 리턴(OAuth2-Client 라이브러리에서 받음) -> AccessToken요청
        System.out.println("getAccessToken: " + userRequest.getAccessToken().getTokenValue());

        // userRequest 정보 -> loadUser 함수 호출 -> 회원프로필 받기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("super.loadUser(userRequest) :" + super.loadUser(userRequest).getAttributes());

        try {
            //return (UserDetailsImpl) process(userRequest, oAuth2User);
            return process(userRequest, oAuth2User);

        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }

    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = OAuth2UserFactory.getOAuth2UserInfo(provider, user.getAttributes());
        Optional<User> savedUser = userRepository.findByUsername(provider + "_" + userInfo.getId());

        if (savedUser.isEmpty()){
            return new UserDetailsImpl(createUser(userInfo, provider), user.getAttributes());
        } else {
            return new UserDetailsImpl(savedUser.get(), user.getAttributes());
        }
    }

    private User createUser(OAuth2UserInfo userInfo, String provider) {
        System.out.println("소셜 로그인 최초 시도");

        String nickname;
        if(userInfo.getFirstName() != null) {
            nickname = userInfo.getFirstName() + userInfo.getLastName();
        } else {
            nickname = userInfo.getName();
        }

        User newUser = User.builder()
                .id(null)
                .username(provider + "_" + userInfo.getId())
                .password(new BCryptPasswordEncoder().encode("social-login-password" + userInfo.getId()))
                .nickname(nickname)
                .email(userInfo.getEmail())
                .profileImg(userInfo.getImageUrl())
                .introDesc("티끌모아 태산!")
                .provider(provider)
                .role(UserRoleEnum.USER)
                .lastEntered(LocalDateTime.now())
                .build();

//        User newUser = User.builder()
//                .id(null)
//                .username(provider + "_" + userInfo.getId())
//                .password(new BCryptPasswordEncoder().encode("social-login-password-google-" + userInfo.getId()))
//                .nickname("")
//                .email("")
//                .profileImg(userInfo.getImageUrl())
//                .introDesc("티끌모아 태산!")
//                .provider(provider)
//                .role(UserRoleEnum.USER)
//                .lastEntered(LocalDateTime.now())
//                .build();

        return userRepository.save(newUser);
    }
}
