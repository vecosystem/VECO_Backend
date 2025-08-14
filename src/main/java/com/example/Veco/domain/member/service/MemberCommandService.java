package com.example.Veco.domain.member.service;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.global.auth.user.userdetails.CustomUserDetails;
import org.springframework.web.multipart.MultipartFile;

public interface MemberCommandService {

    //String updateNickname(Long memberId, String nickname);

    Member saveMember(Member member);
    
    Member updateProfileImage(MultipartFile file, Member member);

    Member deleteProfileImage(Member member);

    Member softDeleteMember(Member member);

    Member withdrawMember(CustomUserDetails customUserDetails);
}
