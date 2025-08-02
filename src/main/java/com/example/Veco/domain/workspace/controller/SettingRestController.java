package com.example.Veco.domain.workspace.controller;

import com.example.Veco.domain.member.converter.MemberConverter;
import com.example.Veco.domain.member.dto.MemberResponseDTO;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.member.service.MemberCommandService;
import com.example.Veco.domain.member.service.MemberQueryService;
import com.example.Veco.domain.team.service.TeamQueryService;
import com.example.Veco.domain.workspace.converter.WorkspaceConverter;
import com.example.Veco.domain.workspace.dto.WorkspaceRequestDTO;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import com.example.Veco.domain.workspace.service.WorkspaceCommandService;
import com.example.Veco.domain.workspace.service.WorkspaceQueryService;
import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.auth.user.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspace")
@Tag(name = "Setting API")
public class SettingRestController {

    private final WorkspaceQueryService workspaceQueryService;
    private final TeamQueryService teamQueryService;
    private final WorkspaceCommandService workspaceCommandService;
    private final MemberRepository memberRepository;
    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;

    /**
     * 유저 프로필 조회 API
     */
    @GetMapping("/setting/my-profile")
    @Operation(
            summary = "유저의 프로필을 조회합니다.",
            description = "profileImage가 null일 경우 기본 이미지를 사용해주세요.")
    public ApiResponse<MemberResponseDTO.ProfileResponseDto> getProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String socialUid = userDetails.getSocialUid();
        Member member = memberQueryService.getMemberBySocialUid(socialUid);

        return ApiResponse.onSuccess(MemberConverter.toProfileResponseDTO(member));
    }

    /**
     * 유저 프로필 이미지 수정 API
     * - 로그인된 유저의 프로필 이미지를 MulipartFile로 수정
     */
    @PatchMapping(value = "/setting/my-profile/profileImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "유저의 프로필 이미지를 수정합니다.")
    public ApiResponse<MemberResponseDTO.MemberProfileImageResponseDto> patchProfileImage(
            @RequestParam MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String socialUid = userDetails.getSocialUid();
        Member member = memberQueryService.getMemberBySocialUid(socialUid);

        Member member1 = memberCommandService.updateProfileImage(image, member);
        return ApiResponse.onSuccess(MemberConverter.toMemberProfileImageResponseDTO(member1));
    }

    /**
     * 유저 프로필 이미지 삭제 API
     */
    @DeleteMapping("/setting/my-profile/profileImage")
    @Operation(summary = "유저의 프로필 이미지를 삭제합니다.")
    public ApiResponse<Void> deleteProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String socialUid = userDetails.getSocialUid();
        Member member = memberQueryService.getMemberBySocialUid(socialUid);

        memberCommandService.deleteProfileImage(member);
        return ApiResponse.onSuccess(null);
    }

    /**
     * 워크스페이스 정보 조회 API
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
     * 워크스페이스 내 팀 목록 조회 (페이징) API
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

        Pageable pageable = PageRequest.of(page, size, Sort.by("order").ascending()); // order를 기준으로 페이지 설정
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

    /**
     * 워크스페이스 내 멤버 전체 조회 API
     * - 멤버별로 속한 팀 정보도 포함
     */
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

    /**
     *
     */
    @PatchMapping("/setting/teams")
    @Operation(summary = "사이드 바의 팀 목록 순서를 수정합니다.")
    public ApiResponse<Void> updateTeamOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody WorkspaceRequestDTO.TeamOrderRequestDto request
    ) {
        String socialUid = userDetails.getSocialUid();
        Member member = memberQueryService.getMemberBySocialUid(socialUid);
        WorkSpace workspace = workspaceQueryService.getWorkSpaceByMember(member);

        workspaceCommandService.updateTeamOrder(workspace, request.getTeamIdList());

        return ApiResponse.onSuccess(null);
    }
}
