package com.example.Veco.domain.issue.controller;

import com.example.Veco.domain.issue.dto.IssueReqDTO;
import com.example.Veco.domain.issue.dto.IssueResponseDTO;
import com.example.Veco.domain.issue.exception.code.IssueErrorCode;
import com.example.Veco.domain.issue.exception.code.IssueSuccessCode;
import com.example.Veco.domain.issue.service.IssueQueryService;
import com.example.Veco.domain.issue.service.command.IssueCommandService;
import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.auth.user.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "이슈 API")
public class IssueController {

    private final IssueQueryService issueQueryService;

    private final IssueCommandService issueCommandService;

    // POST : 이슈 작성
    @Operation(
            summary = "이슈 생성 API",
            description = "이슈를 작성합니다."
    )
    @PostMapping("/teams/{teamId}/issues")
    public ApiResponse<IssueResponseDTO.CreateIssue> createIssue(
            @PathVariable Long teamId,
            @RequestBody IssueReqDTO.CreateIssue dto,
            @AuthenticationPrincipal AuthUser user
    ){
        return ApiResponse.onSuccess(IssueSuccessCode.CREATE, issueCommandService.createIssue(user, teamId, dto));
    }
    // 생성될 이슈 이름 조회
    @Operation(
            summary = "생성될 이슈 이름 조회",
            description = "이슈 생성 시 먼저 보이는 이름(Veco-i3) 을 조회합니다."
    )
    @GetMapping("/teams/{teamId}/issue-name")
    public ApiResponse<String> getIssueName(
            @PathVariable
            Long teamId
    ){
        return ApiResponse.onSuccess(IssueSuccessCode.OK, issueQueryService.getIssueName(teamId));
    }

    // PATCH : 이슈 수정
    @Operation(
            summary = "이슈 수정 API",
            description = "이슈를 수정합니다. " +
                    "변경 사항이 없는 속성은 RequestBody에서 제거 후 요청해주시면 됩니다."
    )
    @PatchMapping("/teams/{teamId}/issues/{issueId}")
    public ApiResponse<IssueResponseDTO.UpdateIssue> updateIssue(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable Long teamId,
            @PathVariable Long issueId,
            @RequestBody IssueReqDTO.UpdateIssue issueDto
    ){
        IssueResponseDTO.UpdateIssue result = issueCommandService.updateIssue(user, issueDto, teamId, issueId);
        if (result != null){
            return ApiResponse.onSuccess(IssueSuccessCode.UPDATE, result);
        } else {
            return ApiResponse.onSuccess(IssueSuccessCode.NO_CONTENT, null);
        }
    }

    @Operation(
            summary = "이슈 삭제 API",
            description = "이슈를 삭제합니다. " +
                    "삭제할 이슈 ID를 리스트 형태로 보내주시면 일괄 삭제 처리 가능합니다."
    )
    @DeleteMapping("/teams/{teamId}/issues")
    public ApiResponse<List<Long>> deleteIssue(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable Long teamId,
            @RequestBody IssueReqDTO.DeleteIssue dto
    ){
        return ApiResponse.onSuccess(IssueSuccessCode.DELETE, issueCommandService.deleteIssue(user,teamId, dto));
    }

    @Operation(
            summary = "팀 내 모든 이슈 조회 API",
            description = "팀의 모든 목표를 조회합니다. 쿼리를 이용해서 필터 적용이 가능합니다." +
                    " 디폴트로 상태(진행 중, 진행 완료)를 기준으로 조회합니다." +
                    "커서 기반 페이지네이션, 최신 순으로 정렬합니다."
    )
    @GetMapping("/teams/{teamId}/issues")
    public ApiResponse<IssueResponseDTO.Pageable<IssueResponseDTO.FilteringIssue<IssueResponseDTO.IssueWithManagers>>> getTeamIssues (
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "-1")
            String cursor,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "불러올 데이터 수는 1 이상이어야 합니다.")
            Integer size,
            @RequestParam(required = false, defaultValue = "state")
            String query
    ) {
        IssueResponseDTO.Pageable<IssueResponseDTO.FilteringIssue<IssueResponseDTO.IssueWithManagers>> result = issueQueryService.getIssuesByTeamId(teamId, cursor, size, query);
        if (result != null) {
            return ApiResponse.onSuccess(IssueSuccessCode.OK, result);
        } else {
            BaseErrorStatus status = IssueErrorCode.NOT_FOUND_IN_TEAM;
            return ApiResponse.onFailure(
                    status.getReasonHttpStatus().getCode(),
                    status.getReasonHttpStatus().getMessage(),
                    null
            );
        }
    }

    @Operation(
            summary = "이슈 상세 조회 API",
            description = "단일 이슈의 상세 정보를 조회합니다."
    )
    @GetMapping("/issues/{issueId}")
    public ApiResponse<IssueResponseDTO.DetailIssue> getTeamIssues (
            @PathVariable Long issueId
    ) {
        return ApiResponse.onSuccess(IssueSuccessCode.OK, issueQueryService.getIssueDetailById(issueId));
    }

    @Operation(
            summary = "팀 내 이슈 간단 조회 API",
            description = "팀의 모든 이슈를 간단히 조회합니다. " +
                    "연결용으로 만들어진 API입니다. "
    )
    @GetMapping("/teams/{teamId}/issues-simple")
    public ApiResponse<IssueResponseDTO.Data<IssueResponseDTO.IssueInfo>> getSimpleIssue(
            @PathVariable
            Long teamId
    ){
        return ApiResponse.onSuccess(IssueSuccessCode.OK, issueQueryService.getSimpleIssue(teamId));
    }
}