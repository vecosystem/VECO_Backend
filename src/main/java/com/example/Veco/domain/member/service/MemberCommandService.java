package com.example.Veco.domain.member.service;

import com.example.Veco.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

public interface MemberCommandService {

    //String updateNickname(Long memberId, String nickname);

    Member saveMember(Member member);
    
    Member updateProfileImage(MultipartFile file, Long memberId);

    void deleteProfileImage(Long memberId);
}
