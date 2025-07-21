package com.example.Veco.global.auth.oauth2.service;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.enums.Provider;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.global.auth.oauth2.CustomOAuth2User;
import com.example.Veco.global.auth.oauth2.dto.OAuth2Attribute;
import com.example.Veco.global.auth.oauth2.userinfo.OAuth2UserInfo;
import com.example.Veco.global.auth.oauth2.util.OAuth2Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        Provider provider = OAuth2Util.getProvider(registrationId);

        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(
                provider, userNameAttributeName, attributes);

        OAuth2UserInfo oAuth2UserInfo = oAuth2Attribute.getOAuth2UserInfo();
        String socialId = oAuth2UserInfo.getSocialId();
        String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();

        Member member = memberRepository.findBySocialUid(socialId)
                .orElse(Member.builder()
                        .email(email)
                        .name(name)
                        .provider(provider)
                        .socialUid(socialId)
                        .build()
                );

        return new CustomOAuth2User(Collections.singleton(new SimpleGrantedAuthority("user")),
                attributes, oAuth2Attribute.getNameAttributeKey(), member);
    }
}
