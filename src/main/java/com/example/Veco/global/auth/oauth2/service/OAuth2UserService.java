package com.example.Veco.global.auth.oauth2.service;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.enums.Provider;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.profile.entity.Profile;
import com.example.Veco.domain.profile.repository.ProfileRepository;
import com.example.Veco.global.auth.oauth2.CustomOAuth2User;
import com.example.Veco.global.auth.oauth2.dto.OAuth2Attribute;
import com.example.Veco.global.auth.oauth2.exception.OAuth2Exeception;
import com.example.Veco.global.auth.oauth2.exception.code.OAuth2ErrorCode;
import com.example.Veco.global.auth.oauth2.userinfo.OAuth2UserInfo;
import com.example.Veco.global.auth.oauth2.util.OAuth2Util;
import com.example.Veco.global.auth.user.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final OAuth2AuthorizedClientService clientService;

    @Value("${spring.security.oauth2.client.provider.kakao.admin-key}")
    private String adminKey;

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
        String profileImage = oAuth2UserInfo.getPicture();

        Optional<Member> optionalMember = memberRepository.findBySocialUid(socialId);

        Member member;
        if (optionalMember.isPresent()) {
            member = optionalMember.get();
        } else {
            Profile profile = Profile.builder()
                    .name(name)
                    .profileImageUrl(profileImage)
                    .build();

            profileRepository.save(profile);

            member = Member.builder()
                    .email(email)
                    .name(name)
                    .nickname(name)
                    .provider(provider)
                    .socialUid(socialId)
                    .profile(profile)
                    .build();

            member = memberRepository.save(member);
        }


        return new CustomOAuth2User(Collections.singleton(new SimpleGrantedAuthority("user")),
                attributes, oAuth2Attribute.getNameAttributeKey(), member);
    }

    public void unlinkGoogleAccess(CustomUserDetails customUserDetails) {

        OAuth2AuthorizedClient authorizedClient = clientService.loadAuthorizedClient(
                "google",
                customUserDetails.getUsername()
        );

        if (authorizedClient != null) {
            String accessToken = authorizedClient.getAccessToken().getTokenValue();

            try {
                String revokeUrl = "https://oauth2.googleapis.com/revoke?token=" + accessToken;

                WebClient.create()
                        .post()
                        .uri(revokeUrl)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

            } catch (Exception e) {
                log.error("구글 연동 해제 실패", e);
                throw new OAuth2Exeception(OAuth2ErrorCode._SOCIAL_UNLINK_FAILED);
            }
        }
    }

    public void unlinkKakaoAccount(Long kakaoId) {
        try {
            String response = WebClient.create("https://kapi.kakao.com")
                    .post()
                    .uri("/v1/user/unlink")
                    .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + adminKey)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue("target_id_type=user_id&target_id=" + kakaoId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            log.error("카카오 연동 해제 실패", e);
            throw new OAuth2Exeception(OAuth2ErrorCode._SOCIAL_UNLINK_FAILED);
        }
    }
}
