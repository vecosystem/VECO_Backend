package com.example.Veco.global.auth.oauth2.dto;

import com.example.Veco.domain.member.enums.Provider;
import com.example.Veco.global.auth.oauth2.userinfo.OAuth2UserInfo;
import com.example.Veco.global.auth.oauth2.util.OAuth2Util;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuth2Attribute {
    private String nameAttributeKey;
    private OAuth2UserInfo oAuth2UserInfo;

    @Builder
    public OAuth2Attribute(String nameAttributeKey, OAuth2UserInfo oAuth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    // OAuth2Utils 를 통해 분기처리 없이 생성된 OAuth2UserInfo 를 반환
    public static OAuth2Attribute of(Provider provider, String userNameAttributeName,
                                     Map<String, Object> attributes) {
        return OAuth2Attribute.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2UserInfo(OAuth2Util.getOAuth2UserInfo(provider, attributes))
                .build();
    }
}
