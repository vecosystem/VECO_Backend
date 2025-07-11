package com.example.Veco.domain.member.converter;

import com.example.Veco.domain.member.dto.MemberResponseDTO;
import com.example.Veco.domain.member.entity.Member;

public class MemberConverter {

    public static MemberResponseDTO.MemberProfileResponseDTO toMemberResponseDTO(Member member) {
        return MemberResponseDTO.MemberProfileResponseDTO.builder()
                .memberId(member.getId())
                .name(member.getName())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .profileImage(member.getProfileImageUrl())
                .build();
    }
}
