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
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberRestController {

    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;

    @GetMapping("/{memberId}")
    @Operation(summary = "유저의 프로필을 조회합니다.")
    public ApiResponse<MemberResponseDTO.ProfileResponseDto> getProfile(@PathVariable Long memberId) {
        Member member = memberQueryService.findById(memberId);
        return ApiResponse.onSuccess(MemberConverter.toProfileResponseDTO(member));
    }

    /*
    @PatchMapping("/{memberId}/nickname")
    @Operation(summary = "유저의 닉네임을 변경합니다.")
    public ApiResponse<MemberResponseDTO.MemberNicknameResponseDto> updateNickname(
            @PathVariable Long memberId,
            @Valid @RequestBody MemberRequestDTO.updateNicknameRequestDto request) {
        Member member = memberQueryService.findById(memberId);
        memberCommandService.updateNickname(memberId, request.getNickname());
        return ApiResponse.onSuccess(MemberConverter.toMemberResponseDTO(member));
    }
    */

    //@PatchMapping(value = "/{memberId}/profileImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
}
