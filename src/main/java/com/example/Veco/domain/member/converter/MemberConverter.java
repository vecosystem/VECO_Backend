package com.example.Veco.domain.member.converter;

import com.example.Veco.domain.member.dto.MemberResponseDTO;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.error.MemberErrorStatus;
import com.example.Veco.domain.member.error.MemberHandler;
import org.springframework.stereotype.Component;

@Component
public class MemberConverter {

    public static MemberResponseDTO.ProfileResponseDto toProfileResponseDTO(Member member) {
        return MemberResponseDTO.ProfileResponseDto.builder()
                .memberId(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .profileImage(member.getProfile().getProfileImageUrl())
                .build();
    }

    public static MemberResponseDTO.MemberProfileImageResponseDto toMemberProfileImageResponseDTO(Member member) {
        return MemberResponseDTO.MemberProfileImageResponseDto.builder()
                .memberId(member.getId())
                .imageUrl(member.getProfile().getProfileImageUrl())
                .build();
    }
}
