package com.example.Veco.domain.goal.controller;

import com.example.Veco.domain.goal.dto.request.GoalReqDTO;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.ChangeGoal;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.CreateGoal;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.Data;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.FilteringGoal;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.FullGoal;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.Pageable;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.SimpleGoal;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.Teammate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "목표 API")
public class GoalController {

    // GET
    // 팀 내 모든 목표 조회
    @Operation(
            summary = "팀 내 모든 목표 조회 API By 김주헌 (개발 중)",
            description = "팀의 모든 목표를 조회합니다. 쿼리를 이용해서 필터 적용이 가능합니다." +
                    " 디폴트로 상태(진행 중, 진행 완료)를 기준으로 조회합니다." +
                    "커서 기반 페이지네이션, 최신 순으로 정렬합니다."
    )
    @GetMapping("/teams/{teamId}/goals")
    public Pageable<FilteringGoal<SimpleGoal>> getTeamGoals(
            @RequestParam @NotBlank(message = "팀 ID는 필수 입력입니다.")
            Long teamId,
            @RequestParam(defaultValue = "-1") @Min(value = -1, message = "커서는 -1보다 큰 정수여야 합니다.")
            String cursor,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "불러올 데이터 수는 1 이상이어야 합니다.")
            Integer size,
            @RequestParam(required = false, defaultValue = "state")
            String query
    ){
        return null;
    }

    // 목표 상세 조회
    @Operation(
            summary = "목표 상세 조회 API By 김주헌 (개발 중)",
            description = "목표 상세 정보를 조회합니다. 댓글 데이터는 최신순으로 정렬되어 있습니다."
    )
    @GetMapping("/goals/{goalId}")
    public FullGoal getGoalDetails(
            @RequestParam @NotBlank(message = "목표 ID는 필수 입력입니다.")
            Long goalId
    ){
        return null;
    }

    // 팀원 조회 (담당자): 변경 가능성 O
    @Operation(
            summary = "팀원 조회 (담당자) API By 김주헌 (개발 중)",
            description = "팀원 목록을 조회합니다." +
                    "담당자를 선택하기 위한 API입니다."
    )
    @GetMapping("/teams/{teamId}/teammate")
    public Data<Teammate> getTeammate(
            @RequestParam @NotBlank(message = "팀 ID는 필수 입력입니다.")
            Long teamId
    ){
        return null;
    }

    // POST
    // 목표 작성: 변경 가능성 O
    @Operation(
            summary = "목표 작성 API By 김주헌 (개발 중)",
            description = "목표를 작성합니다." +
                    "사진같은 경우, 목표 사진 첨부 API를 통해 먼저 URL을 얻은 다음 상세 내용과 합쳐서 보내주세요." +
                    "플로우: 내용 작성 -> 사진 업로드 & URL 반환 -> 마크다운 적용 -> 다른 내용들과 함께 목표 업로드"
    )
    @PostMapping("/teams/{teamId}/goals")
    public CreateGoal createGoal(
            @RequestParam Long teamId,
            @RequestBody GoalReqDTO.CreateGoal dto
    ){
        return null;
    }

    // 목표 사진 첨부: 변경 가능성 O
    @Operation(
            summary = "목표 사진 첨부 API By 김주헌 (개발 중)",
            description = "단일 사진, 파일을 업로드합니다. " +
                    "목표 작성할때 사진을 업로드하기 위해 만들어진 API입니다. "
    )
    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(
            @RequestParam MultipartFile file
    ){
        return null;
    }

    // PATCH
    // 목표 수정
    @Operation(
            summary = "목표 수정 API By 김주헌 (개발 중)",
            description = "목표를 수정합니다. " +
                    "수정할 내용을 추가하면 됩니다. 담당자, 이슈를 수정할 경우 수정된 리스트를 업로드하시면 됩니다. " +
                    "변경 사항이 없는 속성은 Null로 두시면 됩니다."
    )
    @PatchMapping("/teams/{teamId}/goals/{goalId}")
    public ChangeGoal changeGoal(
            @RequestParam Long teamId,
            @RequestParam Long goalId,
            @RequestBody GoalReqDTO.ChangeGoal dto
    ){
        return null;
    }

    // DELETE
    // 목표 삭제
    @Operation(
            summary = "목표 삭제 API By 김주헌 (개발 중)",
            description = "목표를 삭제합니다. (Soft Delete)"
    )
    @DeleteMapping("/teams/{teamId}/goals/{goalId}")
    public String deleteGoal(
            @RequestParam Long teamId,
            @RequestParam Long goalId
    ){
        return null;
    }
}
