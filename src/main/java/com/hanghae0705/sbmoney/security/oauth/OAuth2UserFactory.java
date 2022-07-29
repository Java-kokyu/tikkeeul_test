package com.hanghae0705.sbmoney.security.oauth;


import com.hanghae0705.sbmoney.exception.ApiException;
import com.hanghae0705.sbmoney.exception.ApiRuntimeException;

import java.util.Map;

public class OAuth2UserFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes){
        switch (provider){
            case "google": return new GoogleOAuth2UserInfo(attributes);
            case "kakao": return new KakaoOAuth2UserInfo(attributes);
            default: throw new ApiRuntimeException(ApiException.NO_PROVIDER);
        }
    }
}
