package com.example.Veco.domain.workspace.controller;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.service.MemberQueryService;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.service.TeamQueryService;
import com.example.Veco.domain.workspace.converter.WorkspaceConverter;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import com.example.Veco.domain.workspace.service.WorkspaceQueryService;
import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.auth.oauth2.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/workspace")
@Tag(name = "워크스페이스 API")
public class WorkspaceRestController {

    private final WorkspaceQueryService workspaceQueryService;
    private final TeamQueryService teamQueryService;

    @GetMapping("/setting")
    @Operation(summary = "워크스페이스 정보를 조회합니다.")
    public ApiResponse<WorkspaceResponseDTO.WorkspaceResponseDto> getWorkspaceInfo(@AuthenticationPrincipal CustomOAuth2User user) {

        Member member = user.getMember();
        WorkSpace workspace = workspaceQueryService.getWorkSpaceByMember(member);
        return ApiResponse.onSuccess(WorkspaceConverter.toWorkspaceResponse(workspace));
    }

    @GetMapping("/setting/teams")
    @Operation(summary = "워크스페이스 안의 팀 목록을 조회합니다.")
    public ApiResponse<WorkspaceResponseDTO.WorkspaceTeamListDto> getTeamList(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Member member = user.getMember();
        WorkSpace workspace = workspaceQueryService.getWorkSpaceByMember(member);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        WorkspaceResponseDTO.WorkspaceTeamListDto result = workspaceQueryService.getTeamListByWorkSpace(pageable, workspace);

        return ApiResponse.onSuccess(result);
    }

}
