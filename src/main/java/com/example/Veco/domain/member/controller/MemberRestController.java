package com.example.Veco.domain.member.controller;

import com.example.Veco.domain.member.converter.MemberConverter;
import com.example.Veco.domain.member.dto.MemberRequestDTO;
import com.example.Veco.domain.member.dto.MemberResponseDTO;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.service.MemberCommandService;
import com.example.Veco.domain.member.service.MemberQueryService;
import com.example.Veco.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberRestController {

    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;

    @GetMapping("/{memberId}")
    @Operation(summary = "유저의 프로필을 조회합니다.")
    public ApiResponse<MemberResponseDTO.MemberProfileResponseDto> getProfile(@PathVariable Long memberId) {
        Member member = memberQueryService.findById(memberId);
        return ApiResponse.onSuccess(MemberConverter.toMemberResponseDTO(member));
    }

    @PatchMapping("/{members/{memberId}/nickname}")
    @Operation(summary = "유저의 닉네임을 변경합니다.")
    public ApiResponse<MemberResponseDTO.MemberProfileResponseDto> updateNickname(
            @PathVariable Long memberId,
            @Valid @RequestBody MemberRequestDTO.updateNicknameRequestDto request) {
        Member member = memberQueryService.findById(memberId);
        String updateMemberId = memberCommandService.updateNickname(request.getNickname());
    }
}
