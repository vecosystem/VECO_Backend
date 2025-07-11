package com.example.Veco.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberProfileResponseDto {
        private Long memberId;
        private String name;
        private String nickname;
        private String email;
        private String profileImage; //url 반환, 실제 요청은 form-data
    }
}
