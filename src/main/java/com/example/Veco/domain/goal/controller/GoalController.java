package com.example.Veco.domain.goal.controller;

import com.example.Veco.domain.goal.dto.request.GoalReqDTO;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.*;
import com.example.Veco.domain.goal.exception.code.GoalErrorCode;
import com.example.Veco.domain.goal.exception.code.GoalSuccessCode;
import com.example.Veco.domain.goal.service.command.GoalCommandService;
import com.example.Veco.domain.goal.service.query.GoalQueryService;
import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.auth.user.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "목표 API")
public class GoalController {

    // 리포지토리
    private final GoalCommandService goalCommandService;
    private final GoalQueryService goalQueryService;

    // GET
    // 팀 내 모든 목표 조회
    @Operation(
            summary = "팀 내 모든 목표 조회 API By 김주헌",
            description = "팀의 모든 목표를 조회합니다. 쿼리를 이용해서 필터 적용이 가능합니다." +
                    " 디폴트로 상태(진행 중, 진행 완료)를 기준으로 조회합니다." +
                    "커서 기반 페이지네이션, 최신 순으로 정렬합니다."
    )
    @GetMapping("/teams/{teamId}/goals")
    public ApiResponse<Pageable<FilteringGoal<SimpleGoal>>> getTeamGoals(
            @PathVariable
            Long teamId,
            @RequestParam(defaultValue = "-1")
            String cursor,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "불러올 데이터 수는 1 이상이어야 합니다.")
            Integer size,
            @RequestParam(required = false, defaultValue = "state")
            String query
    ){
        Pageable<FilteringGoal<SimpleGoal>> result = goalQueryService.getGoals(teamId, cursor, size, query);
        if (result != null){
            return ApiResponse.onSuccess(GoalSuccessCode.OK, result);
        } else {
            BaseErrorStatus status = GoalErrorCode.NOT_FOUND_IN_TEAM;
            return ApiResponse.onFailure(
                    status.getReasonHttpStatus().getCode(),
                    status.getReasonHttpStatus().getMessage(),
                    null
            );
        }
    }

    // 목표 간단 조회 (이슈-목표 연결용)
    @Operation(
            summary = "팀 내 목표 간단 조회 API By 김주헌",
            description = "팀의 모든 목표를 간단히 조회합니다. " +
                    "이슈에서 목표를 연결하기 위해 만들어진 API입니다. "
    )
    @GetMapping("/teams/{teamId}/goals-simple")
    public ApiResponse<Data<GoalInfo>> getSimpleGoal(
            @PathVariable
            Long teamId
    ){
        return ApiResponse.onSuccess(GoalSuccessCode.OK, goalQueryService.getSimpleGoal(teamId));
    }

    // 목표 상세 조회
    @Operation(
            summary = "목표 상세 조회 API By 김주헌",
            description = "목표 상세 정보를 조회합니다. 댓글 데이터는 최신순으로 정렬되어 있습니다."
    )
    @GetMapping("/goals/{goalId}")
    public ApiResponse<FullGoal> getGoalDetail(
            @PathVariable
            Long goalId
    ){
        return ApiResponse.onSuccess(GoalSuccessCode.OK, goalQueryService.getGoalDetail(goalId));
    }

    // 팀원 조회 (담당자): 변경 가능성 O
    @Operation(
            summary = "팀원 조회 (담당자) API By 김주헌",
            description = "팀원 목록을 조회합니다." +
                    "담당자를 선택하기 위한 API입니다."
    )
    @GetMapping("/teams/{teamId}/teammate")
    public ApiResponse<Data<Teammate>> getTeammate(
            @PathVariable
            Long teamId
    ){
        Data<Teammate> teammate = goalQueryService.getTeammate(teamId);
        if (teammate != null){
            return ApiResponse.onSuccess(GoalSuccessCode.OK, teammate);
        } else {
            return ApiResponse.onSuccess(GoalSuccessCode.NO_CONTENT, null);
        }
    }

    // 생성될 목표 이름 조회
    @Operation(
            summary = "생성될 목표 이름 조회 API By 김주헌",
            description = "목표 생성 시 먼저 보이는 이름(Veco-g3) 을 조회합니다."
    )
    @GetMapping("/teams/{teamId}/goal-name")
    public ApiResponse<String> getGoalName(
            @PathVariable
            Long teamId
    ){
        return ApiResponse.onSuccess(GoalSuccessCode.OK, goalQueryService.getGoalName(teamId));
    }

    // 삭제된 목표 조회
    @Operation(
            summary = "삭제된 목표 조회 API By 김주헌",
            description = "삭제된 목표들을 조회합니다." +
                    "기준이 될 팀 ID를 쿼리로 보내주세요."
    )
    @GetMapping("/deleted-goals")
    public ApiResponse<List<GoalInfo>> getDeletedGoals(
            @RequestParam Long teamId
    ){
        return ApiResponse.onSuccess(goalQueryService.getDeletedGoals(teamId));
    }

    // POST
    // 목표 작성: 변경 가능성 O
    @Operation(
            summary = "목표 작성 API By 김주헌",
            description = "목표를 작성합니다."
    )
    @PostMapping("/teams/{teamId}/goals")
    public ApiResponse<CreateGoal> createGoal(
            @PathVariable Long teamId,
            @RequestBody GoalReqDTO.CreateGoal dto,
            @AuthenticationPrincipal AuthUser user
    ){
        return ApiResponse.onSuccess(GoalSuccessCode.CREATE, goalCommandService.createGoal(teamId, dto, user));
    }

    // 목표 사진 첨부: 변경 가능성 O
    @Operation(
            summary = "목표 사진 첨부 API By 김주헌 (사용 X)",
            description = "단일 사진, 파일을 업로드합니다."
    )
    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadFile(
            @RequestParam MultipartFile file
    ){
        return ApiResponse.onSuccess(GoalSuccessCode.IMAGE_UPLOAD, goalCommandService.uploadFile(file));
    }

    // 삭제된 목표 복원
    @Operation(
            summary = "삭제된 목표 복원 API By 김주헌",
            description = "삭제된 목표를 복원합니다."
    )
    @PostMapping("/goals/restore")
    public ApiResponse<List<GoalInfo>> restoreGoals(
            @RequestBody GoalReqDTO.DeleteGoal dto
    ){
        return ApiResponse.onSuccess(goalCommandService.restoreGoals(dto));
    }

    // PATCH
    // 목표 수정
    @Operation(
            summary = "목표 수정 API By 김주헌",
            description = "목표를 수정합니다. " +
                    "수정할 내용을 추가하면 됩니다. 담당자, 이슈를 수정할 경우 수정된 리스트를 업로드하시면 됩니다. " +
                    "변경 사항이 없는 속성은 RequestBody에서 제거 후 요청하면 됩니다."
    )
    @PatchMapping("/teams/{teamId}/goals/{goalId}")
    public ApiResponse<UpdateGoal> updateGoal(
            @PathVariable Long teamId,
            @PathVariable Long goalId,
            @RequestBody GoalReqDTO.UpdateGoal dto,
            @AuthenticationPrincipal AuthUser user
    ){
        UpdateGoal result = goalCommandService.updateGoal(dto, teamId, goalId, user);
        if (result != null){
            return ApiResponse.onSuccess(GoalSuccessCode.UPDATE, result);
        } else {
            return ApiResponse.onSuccess(GoalSuccessCode.NO_CONTENT, null);
        }
    }

    // DELETE
    // 목표 삭제
    @Operation(
            summary = "목표 삭제 API By 김주헌",
            description = "목표를 삭제합니다. (Soft Delete)" +
                    "삭제할 목표 ID를 리스트 형태로 보내주시면 일괄 삭제 처리 가능합니다."
    )
    @DeleteMapping("/teams/{teamId}/goals")
    public ApiResponse<List<Long>> deleteGoal(
            @PathVariable Long teamId,
            @RequestBody GoalReqDTO.DeleteGoal dto,
            @AuthenticationPrincipal AuthUser user
    ){
        return ApiResponse.onSuccess(GoalSuccessCode.DELETE, goalCommandService.deleteGoal(teamId, dto, user));
    }
}
