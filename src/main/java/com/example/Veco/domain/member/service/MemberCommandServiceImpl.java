package com.example.Veco.domain.member.service;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.repository.MemberRepository;
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
    public Member updateProfileImage(MultipartFile file, Long memberId) {
        Member member = memberQueryService.findById(memberId);

        // S3 업로드 및 URL 생성
        String uploadedPath = s3Util.uploadFile(List.of(file), "profile/").get(0);
        String imageUrl = s3Util.getImageUrl(uploadedPath);

        // DB 업데이트
        member.getProfile().updateProfileImageUrl(imageUrl);
        return member;
    }

    @Transactional
    @Override
    public void deleteProfileImage(Long memberId) {
        Member member = memberQueryService.findById(memberId);

        String imageUrl = member.getProfile().getProfileImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            s3Util.deleteFile(imageUrl);
        }

        member.getProfile().updateProfileImageUrl(null);
    }

    @Override
    @Transactional
    public Member saveMember(Member member) {
        Member savedMember = memberRepository.save(member);

        // DTO로 변환하여 반환
        return savedMember;
    }
}
