package com.example.Veco.domain.member.service;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.error.MemberErrorStatus;
import com.example.Veco.domain.member.error.MemberHandler;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.global.aws.exception.S3Exception;
import com.example.Veco.global.aws.exception.code.S3ErrorCode;
import com.example.Veco.global.aws.util.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService {

    private final MemberRepository memberRepository;
    private final MemberQueryService memberQueryService;
    private final S3Util s3Util;


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
        String uploadedPath = s3Util.uploadFile(List.of(file), "profile/").get(0);
        String imageUrl = s3Util.getImageUrl(uploadedPath);

        // DB 업데이트
        member.getProfile().updateProfileImageUrl(imageUrl);
        return member;
    }

    @Transactional
    @Override
    public void deleteProfileImage(Member member) {
        //프로필 존재 확인
        if (member.getProfile() == null) {
            throw new MemberHandler(MemberErrorStatus._PROFILE_NOT_FOUND);
        }

        if (member.getProfile().getProfileImageUrl() == null) {
            throw new S3Exception(MemberErrorStatus._PROFILE_IMAGE_NOT_FOUND);
        }

        String imageUrl = member.getProfile().getProfileImageUrl();
        s3Util.deleteFile(imageUrl);

        member.getProfile().updateProfileImageUrl(null);
    }

    @Override
    @Transactional
    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }
}
