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
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.Map;

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

    @PatchMapping(value = "/{memberId}/profileImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "유저의 프로필 이미지를 수정합니다.")
    public ApiResponse<MemberResponseDTO.MemberProfileImageResponseDto> patchProfileImage(
            @PathVariable Long memberId,
            @RequestParam MultipartFile image
    ) {
        Member member = memberCommandService.updateProfileImage(image, memberId);
        return ApiResponse.onSuccess(MemberConverter.toMemberProfileImageResponseDTO(member));
    }

    @DeleteMapping("/{memberId}/profileImage")
    @Operation(summary = "유저의 프로필 이미지를 삭제합니다.")
    public ApiResponse<Void> deleteProfileImage(@PathVariable Long memberId) {
        memberCommandService.deleteProfileImage(memberId);
        return ApiResponse.onSuccess(null);
    }
}
