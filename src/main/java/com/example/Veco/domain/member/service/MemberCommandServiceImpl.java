package com.example.Veco.domain.member.service;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.enums.Provider;
import com.example.Veco.domain.member.error.MemberErrorStatus;
import com.example.Veco.domain.member.error.MemberHandler;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.profile.entity.Profile;
import com.example.Veco.global.auth.oauth2.service.OAuth2UserService;
import com.example.Veco.global.auth.user.userdetails.CustomUserDetails;
import com.example.Veco.global.aws.exception.S3Exception;
import com.example.Veco.global.aws.exception.code.S3ErrorCode;
import com.example.Veco.global.aws.util.S3Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberCommandServiceImpl implements MemberCommandService {

    private final MemberRepository memberRepository;
    private final MemberQueryService memberQueryService;
    private final S3Util s3Util;
    private final OAuth2AuthorizedClientService clientService;
    private final OAuth2UserService oAuth2UserService;


    @Transactional
    @Override
    public Member updateProfileImage(MultipartFile file, Member member) {
        // 프로필 존재 확인
        if (member.getProfile() == null) {
            throw new MemberHandler(MemberErrorStatus._PROFILE_NOT_FOUND);
        }

        // 파일 유효성 검사
        if (file == null || file.isEmpty()) {
            throw new S3Exception(S3ErrorCode.NOT_FOUND_FILE);
        }

        // S3 업로드 및 URL 생성
        String uploadedPath = s3Util.uploadFile(file, null);
        String imageUrl = s3Util.getImageUrl(uploadedPath);

        // DB 업데이트
        member.getProfile().updateProfileImageUrl(imageUrl);
        return member;
    }

    @Transactional
    @Override
    public Member deleteProfileImage(Member member) {
        Profile profile = member.getProfile();
        //프로필 존재 확인
        if (profile == null) {
            throw new MemberHandler(MemberErrorStatus._PROFILE_NOT_FOUND);
        }

        if (profile.getProfileImageUrl() == null || profile.getProfileImageUrl().isEmpty() || profile.getProfileImageUrl().equals("https://s3.ap-northeast-2.amazonaws.com/s3.veco/default/default-profile.png")) {
            throw new S3Exception(MemberErrorStatus._PROFILE_IMAGE_NOT_FOUND);
        }

        String imageUrl = member.getProfile().getProfileImageUrl();

        if (imageUrl == null || imageUrl.isBlank()) {
            throw new S3Exception(MemberErrorStatus._PROFILE_IMAGE_NOT_FOUND);
        }

        s3Util.deleteFile(imageUrl);

        member.getProfile().updateProfileImageUrl("https://s3.ap-northeast-2.amazonaws.com/s3.veco/default/default-profile.png");
        return member;
    }

    @Override
    @Transactional
    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    @Transactional
    @Override
    public Member softDeleteMember(Member member) {
        member.softDelete();

        return memberRepository.save(member);
    }

    @Override
    public Member withdrawMember(CustomUserDetails customUserDetails) {

        Member member = memberRepository.findBySocialUid(customUserDetails.getSocialUid())
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND));

        Provider provider = member.getProvider();

        if (provider.equals(Provider.GOOGLE)) {
            oAuth2UserService.unlinkGoogleAccess(customUserDetails);
        } else if (provider.equals(Provider.KAKAO)) {
            oAuth2UserService.unlinkKakaoAccount(Long.parseLong(customUserDetails.getSocialUid()));
        }

        // Spring Security에서 인증된 클라이언트 정보 제거
        clientService.removeAuthorizedClient(provider.toString().toLowerCase(), customUserDetails.getUsername());

        // DB에서 사용자 정보 삭제 (soft delete)
        return softDeleteMember(member);
    }
}
