package com.example.Veco.domain.member.converter;

import com.example.Veco.domain.member.dto.MemberRequestDTO;
import com.example.Veco.domain.member.dto.MemberResponseDTO;
import com.example.Veco.domain.member.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberConverter {

    public static MemberResponseDTO.MemberProfileResponseDto toMemberProfileResponseDTO(Member member) {
        return MemberResponseDTO.MemberProfileResponseDto.builder()
                .memberId(member.getId())
                .name(member.getName())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .profileImage(member.getProfileImageUrl())
                .build();
    }

    public static MemberResponseDTO.MemberNicknameResponseDto toMemberResponseDTO(Member member) {
        return MemberResponseDTO.MemberNicknameResponseDto.builder()
                .memberId(member.getId())
                .build();
    }
}
