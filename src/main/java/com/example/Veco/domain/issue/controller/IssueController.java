package com.example.Veco.domain.issue.controller;

import com.example.Veco.domain.issue.service.IssueQueryService;
import com.example.Veco.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name="이슈 API")
public class IssueController {

    private final IssueQueryService issueQueryService;

    @Operation(
            summary = "팀 내 모든 이슈 조회 API",
            description = "팀의 모든 목표를 조회합니다. 쿼리를 이용해서 필터 적용이 가능합니다." +
                    " 디폴트로 상태(진행 중, 진행 완료)를 기준으로 조회합니다." +
                    "커서 기반 페이지네이션, 최신 순으로 정렬합니다."
    )
    @GetMapping("/teams/{teamId}/issues")
    public ApiResponse<?> getTeamIssues (
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "-1") @Min(value = -1, message = "커서는 -1보다 큰 정수여야 합니다.")
            String cursor,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "불러올 데이터 수는 1 이상이어야 합니다.")
            Integer size,
            @RequestParam(required = false, defaultValue = "state")
            String query
    ) {
        return ApiResponse.onSuccess(issueQueryService.getIssuesByTeamId(teamId, cursor, size, query));
    }



}
