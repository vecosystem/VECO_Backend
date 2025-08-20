package com.example.Veco.domain.workspace.controller;

import com.example.Veco.domain.member.converter.MemberConverter;
import com.example.Veco.domain.member.dto.MemberResponseDTO;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.service.MemberCommandService;
import com.example.Veco.domain.member.service.MemberQueryService;
import com.example.Veco.domain.workspace.converter.WorkspaceConverter;
import com.example.Veco.domain.workspace.dto.WorkspaceRequestDTO;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import com.example.Veco.domain.workspace.error.SettingSuccessCode;
import com.example.Veco.domain.workspace.service.WorkspaceCommandService;
import com.example.Veco.domain.workspace.service.WorkspaceQueryService;
import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.auth.jwt.util.JwtUtil;
import com.example.Veco.global.auth.user.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
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
    private final WorkspaceCommandService workspaceCommandService;
    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;
    private final JwtUtil jwtUtil;

    /**
     * 유저 프로필 조회 API
     * - 로그인된 유저의 프로필 정보를 조회
     * - 응답에 profileImage가 null인 경우 프론트에서 기본 이미지 사용
     */
    @GetMapping("/setting/my-profile")
    @Operation(
            summary = "유저의 프로필을 조회합니다.",
            description = "profileImage가 null일 경우 기본 이미지를 사용해주세요.")
    public ApiResponse<MemberResponseDTO.ProfileResponseDto> getProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = memberQueryService.getMemberBySocialUid(userDetails.getSocialUid());

        return ApiResponse.onSuccess(MemberConverter.toProfileResponseDTO(member));
    }

    /**
     * 유저 프로필 이미지 수정 API
     * - 로그인된 유저의 프로필 이미지를 MulipartFile로 수정
     */
    @PatchMapping(value = "/setting/my-profile/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "유저의 프로필 이미지를 수정합니다.")
    public ApiResponse<MemberResponseDTO.MemberProfileImageResponseDto> patchProfileImage(
            @RequestParam MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = memberQueryService.getMemberBySocialUid(userDetails.getSocialUid());

        Member member1 = memberCommandService.updateProfileImage(image, member);
        return ApiResponse.onSuccess(MemberConverter.toMemberProfileImageResponseDTO(member1));
    }

    /**
     * 유저 프로필 이미지 삭제 API
     */
    @DeleteMapping("/setting/my-profile/profile-image")
    @Operation(summary = "유저의 프로필 이미지를 삭제합니다.")
    public ApiResponse<MemberResponseDTO.MemberProfileImageResponseDto> deleteProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = memberQueryService.getMemberBySocialUid(userDetails.getSocialUid());

        Member member1 = memberCommandService.deleteProfileImage(member);
        return ApiResponse.onSuccess(MemberConverter.toMemberProfileImageResponseDTO(member1));
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
        Member member = memberQueryService.getMemberBySocialUid(userDetails.getSocialUid());

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
        WorkSpace workspace = workspaceQueryService.getWorkspaceBySocialUid(userDetails.getSocialUid());

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
            @RequestBody @Valid WorkspaceRequestDTO.CreateTeamRequestDto request // 팀 이름 + 멤버 ID 리스트
    ) {
        WorkSpace workspace = workspaceQueryService.getWorkspaceBySocialUid(userDetails.getSocialUid());

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
        Member member = memberQueryService.getMemberBySocialUid(userDetails.getSocialUid());

        List<WorkspaceResponseDTO.WorkspaceMemberWithTeamsDto> result =
                workspaceQueryService.getWorkspaceMembers(member);

        List<WorkspaceResponseDTO.WorkspaceMemberWithTeamsDto> sorted = result.stream()
                .sorted((m1, m2) -> {
                    if (m1.getMemberId().equals(member.getId())) return -1;  //나 자신은 항상 상단
                    if (m2.getMemberId().equals(member.getId())) return 1;
                    return m1.getJoinedAt().compareTo(m2.getJoinedAt()); //참여일 순
                })
                .toList();

        return ApiResponse.onSuccess(sorted);
    }

    /**
     * 사이드 바 팀 목록 순서 수정 API
     * - 사이드바에서 표시된 팀들의 순서를 수정
     * - 요청으로 전달받은 팀 ID 리스트 순서대로 정렬
     * - 실제 팀 개수와 요청으로 전달받은 팀 개수가 같아야함
     */
    @PatchMapping("/setting/teams")
    @Operation(summary = "사이드 바의 팀 목록 순서를 수정합니다.")
    public ApiResponse<Void> updateTeamOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid WorkspaceRequestDTO.TeamOrderRequestDto request
    ) {
        WorkSpace workspace = workspaceQueryService.getWorkspaceBySocialUid(userDetails.getSocialUid());

        workspaceCommandService.updateTeamOrder(workspace, request.getTeamIdList());

        return ApiResponse.onSuccess(null);
    }

    /**
     * 워크스페이스 초대 정보 조회 API
     * - 초대 링크와 암호를 반환
     */
    @GetMapping("/setting/invite")
    @Operation(
            summary = "워크스페이스에 팀원을 초대합니다.",
            description = "초대링크와 암호를 보여줍니다."
    )
    public ApiResponse<WorkspaceResponseDTO.InviteInfoResponseDto> inviteWorkspace(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = memberQueryService.getMemberBySocialUid(userDetails.getSocialUid());
        WorkSpace workspace = workspaceQueryService.getWorkspaceBySocialUid(userDetails.getSocialUid());

        return ApiResponse.onSuccess(WorkspaceConverter.toInviteInfoResponseDto(workspace, member));
    }

    @DeleteMapping("/setting/my-profile")
    @Operation(summary = "유저의 계정을 삭제합니다.")
    public ApiResponse<?> softDeleteMember(
            HttpServletRequest request,
            HttpServletResponse response,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = memberQueryService.getMemberBySocialUid(userDetails.getSocialUid());
        memberCommandService.withdrawMember(userDetails);

        // 액세스 토큰 블랙리스트 처리
        String token = request.getHeader("Authorization")
                .replace("Bearer ", "");
        jwtUtil.setBlackList(token);

        // 리프레쉬 토큰 쿠키 삭제
        ResponseCookie refreshTokenCookie = jwtUtil.expireRefreshTokenCookie();
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ApiResponse.onSuccess(SettingSuccessCode.DELETE, null);
    }
}
