package com.example.Veco.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

public class MemberResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileResponseDto {
        private Long memberId;
        private String name;
        private String email;
        private String profileImage; //url 반환, 실제 요청은 form-data
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberNicknameResponseDto {
        private Long memberId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberProfileImageResponseDto {
        private Long memberId;
        private String imageUrl;
    }
}
