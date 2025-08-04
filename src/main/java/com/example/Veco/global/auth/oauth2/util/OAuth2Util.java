package com.example.Veco.global.auth.oauth2.util;

import com.example.Veco.domain.member.enums.Provider;
import com.example.Veco.global.auth.oauth2.userinfo.GoogleOAuth2UserInfo;
import com.example.Veco.global.auth.oauth2.userinfo.KakaoOAuth2UserInfo;
import com.example.Veco.global.auth.oauth2.userinfo.OAuth2UserInfo;

import java.util.Map;

public class OAuth2Util {
    // registrationID 를 보고 어떤 소셜에서 인증을 했는지 반환
    public static Provider getProvider(String registrationId) {
        if (registrationId != null) {
            registrationId = registrationId.toUpperCase();
        }

        if ("GOOGLE".equals(registrationId)) {
            return Provider.GOOGLE;
        } else if ("KAKAO".equals(registrationId)) {
            return Provider.KAKAO;
        }
        return null;
    }

    public static OAuth2UserInfo getOAuth2UserInfo(Provider provider, Map<String, Object> attributes) {
        switch (provider) {
            case GOOGLE:
                return new GoogleOAuth2UserInfo(attributes);
            case KAKAO:
                return new KakaoOAuth2UserInfo(attributes);
        }
        return null;
    }
}
