package com.example.Veco.domain.member.converter;

import com.example.Veco.domain.member.dto.MemberRequestDTO;
import com.example.Veco.domain.member.dto.MemberResponseDTO;
import com.example.Veco.domain.member.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberConverter {

    public static MemberResponseDTO.MemberProfileResponseDto toMemberResponseDTO(Member member) {
        return MemberResponseDTO.MemberProfileResponseDto.builder()
                .memberId(member.getId())
                .name(member.getName())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .profileImage(member.getProfileImageUrl())
                .build();
    }

    public static MemberResponseDTO.MemberProfileResponseDto toMemberResponseDTO(Long memberId) {
        return MemberResponseDTO.MemberProfileResponseDto.builder()
                .memberId(memberId)
                .build();
    }

    public static MemberRequestDTO.updateNicknameRequestDto toUpdateNicknameRequestDto(Member member) {
        return MemberRequestDTO.updateNicknameRequestDto.builder()
                .nickname(member.getNickname())
                .build();
    }
}
