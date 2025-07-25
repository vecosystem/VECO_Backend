package com.example.Veco.domain.workspace.controller;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.member.service.MemberQueryService;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.service.TeamQueryService;
import com.example.Veco.domain.workspace.converter.WorkspaceConverter;
import com.example.Veco.domain.workspace.dto.WorkspaceRequestDTO;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import com.example.Veco.domain.workspace.service.WorkspaceCommandService;
import com.example.Veco.domain.workspace.service.WorkspaceQueryService;
import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.auth.oauth2.CustomOAuth2User;
import com.example.Veco.global.auth.user.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/workspace")
@Tag(name = "워크스페이스 API")
public class WorkspaceRestController {

    private final WorkspaceQueryService workspaceQueryService;
    private final TeamQueryService teamQueryService;
    private final WorkspaceCommandService workspaceCommandService;
    private final MemberRepository memberRepository;
    private final MemberQueryService memberQueryService;

    /**
     * 워크스페이스 정보 조회
     * - 로그인한 멤버의 워크스페이스 정보를 반환
     */
    @GetMapping("/setting")
    @Operation(summary = "워크스페이스 정보를 조회합니다.")
    public ApiResponse<WorkspaceResponseDTO.WorkspaceResponseDto> getWorkspaceInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails // 로그인된 사용자 정보
    ) {
        String socialUid = userDetails.getSocialUid();
        Member member = memberQueryService.getMemberBySocialUid(socialUid);

        WorkSpace workspace = workspaceQueryService.getWorkSpaceByMember(member); // 워크스페이스 조회
        return ApiResponse.onSuccess(WorkspaceConverter.toWorkspaceResponse(workspace));
    }

    /**
     * 워크스페이스 내 팀 목록 조회 (페이징)
     * - 팀 ID 기준 내림차순 정렬
     * - 각 팀의 멤버 수 포함
     */
    @GetMapping("/setting/teams")
    @Operation(summary = "워크스페이스 안의 팀 목록을 조회합니다.")
    public ApiResponse<WorkspaceResponseDTO.WorkspaceTeamListDto> getTeamList(
            @AuthenticationPrincipal CustomUserDetails userDetails, // 로그인 유저
            @RequestParam(defaultValue = "0") int page,     // 페이지 번호 (기본 0)
            @RequestParam(defaultValue = "20") int size     // 페이지 크기 (기본 20)
    ) {
        String socialUid = userDetails.getSocialUid();
        Member member = memberQueryService.getMemberBySocialUid(socialUid);

        WorkSpace workspace = workspaceQueryService.getWorkSpaceByMember(member);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending()); // 페이지 설정
        WorkspaceResponseDTO.WorkspaceTeamListDto result =
                workspaceQueryService.getTeamListByWorkSpace(pageable, workspace); // 팀 목록 조회

        return ApiResponse.onSuccess(result);
    }

    /**
     * 워크스페이스 내 팀 생성 API
     * - 팀 이름 및 멤버 ID 리스트를 요청 바디로 받아 팀 생성
     */
    @PostMapping("/setting/teams")
    @Operation(summary = "워크스페이스 안에 팀을 생성합니다.")
    public ApiResponse<WorkspaceResponseDTO.CreateTeamResponseDto> createTeam(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody WorkspaceRequestDTO.CreateTeamRequestDto request // 팀 이름 + 멤버 ID 리스트
    ) {

        String socialUid = userDetails.getSocialUid();
        Member member = memberQueryService.getMemberBySocialUid(socialUid);

        WorkSpace workspace = workspaceQueryService.getWorkSpaceByMember(member);

        WorkspaceResponseDTO.CreateTeamResponseDto response =
                workspaceCommandService.createTeam(workspace, request); // 팀 생성

        return ApiResponse.onSuccess(response); // 생성된 팀 정보 + 멤버 목록 반환
    }

    @GetMapping("/setting/members")
    @Operation(summary = "워크스페이스 내의 멤버 정보를 조회합니다.")
    public ApiResponse<List<WorkspaceResponseDTO.WorkspaceMemberWithTeamsDto>> getWorkspaceMembers(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String socialUid = userDetails.getSocialUid();
        Member member = memberQueryService.getMemberBySocialUid(socialUid);

        List<WorkspaceResponseDTO.WorkspaceMemberWithTeamsDto> result =
                workspaceQueryService.getWorkspaceMembers(member);

        return ApiResponse.onSuccess(result);
    }
}
