package com.example.Veco.domain.workspace.controller;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.service.MemberQueryService;
import com.example.Veco.domain.workspace.converter.WorkspaceConverter;
import com.example.Veco.domain.workspace.dto.WorkspaceRequestDTO;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO.JoinWorkspace;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import com.example.Veco.domain.workspace.error.WorkspaceSuccessCode;
import com.example.Veco.domain.workspace.service.WorkspaceCommandService;
import com.example.Veco.domain.workspace.service.WorkspaceQueryService;
import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.auth.user.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspace")
@Tag(name = "워크스페이스 API")
public class WorkspaceRestController {

    private final WorkspaceQueryService workspaceQueryService;
    private final WorkspaceCommandService workspaceCommandService;
    private final MemberQueryService memberQueryService;

    @PostMapping("/create-url")
    @Operation(summary = "워크스페이스 이름에 맞는 url를 미리보기합니다.")
    public ApiResponse<WorkspaceResponseDTO.PreviewUrlResponseDto> createWorkspaceUrl(@Valid @RequestBody WorkspaceRequestDTO.PreviewUrlRequestDto request) {
        String previewUrl = workspaceQueryService.createPreviewUrl(request.getWorkspaceName());
        return ApiResponse.onSuccess(WorkspaceConverter.toPreviewUrlResponseDto(previewUrl));
    }

    @PostMapping("")
    @Operation(summary = "워크스페이스를 생성합니다.")
    public ApiResponse<WorkspaceResponseDTO.CreateWorkspaceResponseDto> createWorkspace(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody WorkspaceRequestDTO.CreateWorkspaceRequestDto request) {
        String socialUid = userDetails.getSocialUid();
        Member member = memberQueryService.getMemberBySocialUid(socialUid);

         return ApiResponse.onSuccess(workspaceCommandService.createWorkspace(member, request));
    }

    // 워크스페이스 참여
    @Operation(
            summary = "워크스페이스 참여 API By 김주헌",
            description = "초대 토큰과 비밀번호를 통해 워크스페이스에 참여합니다."
    )
    @PostMapping("/join")
    public ApiResponse<JoinWorkspace> joinWorkspace(
            @RequestBody WorkspaceRequestDTO.JoinWorkspace dto,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        return ApiResponse.onSuccess(
                WorkspaceSuccessCode.OK,
                workspaceCommandService.joinWorkspace(dto, user)
        );
    }
}
