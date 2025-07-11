package com.example.Veco.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class MemberRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class updateNicknameRequestDto {
        @NotBlank(message = "닉네임은 비어 있을 수 없습니다.")
        private String nickname;
    }
}
