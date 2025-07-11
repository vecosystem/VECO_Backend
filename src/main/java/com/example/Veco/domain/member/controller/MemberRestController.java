package com.example.Veco.domain.member.controller;

import com.example.Veco.domain.member.converter.MemberConverter;
import com.example.Veco.domain.member.dto.MemberResponseDTO;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.service.MemberQueryService;
import com.example.Veco.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberRestController {

    private final MemberQueryService memberQueryService;

    @GetMapping("/{memberId}")
    @Operation(summary = "유저의 프로필을 조회합니다.")
    public ApiResponse<MemberResponseDTO.MemberProfileResponseDTO> getProfile(@PathVariable Long memberId) {
        Member member = memberQueryService.findById(memberId);
        return ApiResponse.onSuccess(MemberConverter.toMemberResponseDTO(member));
    }
}
