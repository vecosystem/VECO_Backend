package com.example.Veco.domain.external.controller;

import com.example.Veco.domain.external.dto.paging.ExternalCursorPageResponse;
import com.example.Veco.domain.external.dto.request.ExternalRequestDTO;
import com.example.Veco.domain.external.dto.response.ExternalApiResponse;
import com.example.Veco.domain.external.dto.response.ExternalGroupedResponseDTO;
import com.example.Veco.domain.external.dto.response.ExternalResponseDTO;
import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.auth.user.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ExternalSwaggerDocs {


    @Operation(
            summary = "팀 내 외부 이슈 간단 조회",
            description = "팀의 모든 외부 이슈를 간단히 조회합니다."
    )
    ApiResponse<ExternalResponseDTO.SimpleListDTO> getSimpleExternals(@PathVariable Long teamId);

    @Operation(
            summary = "팀 내 외부이슈 상세조회 API",
            description = "외부이슈 식별자를 통해서 외부이슈의 상세 데이터를 가져옵니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "외부이슈 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = ExternalResponseDTO.ExternalInfoDTO.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "외부이슈를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "외부이슈 없음",
                                    value = "{\"isSuccess\": false, \"code\": \"EXTERNAL400\", \"message\": \"해당하는 외부 이슈가 존재하지 않습니다.\", \"result\": null}"
                            )
                    )
            )
    })
    ApiResponse<ExternalResponseDTO.ExternalInfoDTO> getExternal(@Parameter(description = "외부이슈 ID", required = true) @PathVariable Long externalId, @PathVariable Long teamId);

    @Operation(
            summary = "팀 내 외부이슈 전체 조회 API",
            description = "팀 내에서 생성된 외부이슈들을 모두 조회하는 API입니다. 커서기반 페이지네이션으로 전달하고, " +
                    "필터조건으로 상태, 담당자, 우선순위, 목표, 외부 연동 툴을 받습니다. 한번에 하나의 필터만 적용가능합니다. 초기 조회 시 커서를 비워두시고, 이어서 조회할 때, 전달받은 다음 커서 값을 파라미터로 넘겨주시면 됩니다."
    )
    ApiResponse<ExternalGroupedResponseDTO.ExternalGroupedPageResponse> getExternals(
            @Parameter(description = "팀 ID", required = true) @PathVariable("teamId") Long teamId,
            @Parameter(description = "필터 타입 (STATE, PRIORITY, ASSIGNEE, GOAL, EXT_TYPE)", required = false) @RequestParam(value = "query", defaultValue = "STATE") ExternalRequestDTO.ExternalGroupedSearchRequestDTO.FilterType query,
            @Parameter(description = "페이징 커서 (다음 페이지 조회용)", required = false) @RequestParam(value = "cursor", required = false) String cursor,
            @Parameter(description = "페이지 크기 (기본값: 50)", required = false) @RequestParam(value = "size", defaultValue = "50") Integer size);

    @Operation(
            summary = "팀 내에서 외부관련 이슈를 생성하는 API",
            description = "팀 내에서 외부 연동 툴과 관련된 이슈를 생성합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "외부이슈 생성 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "유효성 검증 실패",
                                    value = "{\"isSuccess\": false, \"code\": \"COMMON400\", \"message\": \"잘못된 요청입니다.\", \"result\": null}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "팀 또는 목표를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "팀 없음",
                                            value = "{\"isSuccess\": false, \"code\": \"TEAM400\", \"message\": \"해당하는 팀이 존재하지 않습니다.\", \"result\": null}"
                                    ),
                                    @ExampleObject(
                                            name = "목표 없음",
                                            value = "{\"isSuccess\": false, \"code\": \"GOAL400\", \"message\": \"해당하는 목표가 존재하지 않습니다.\", \"result\": null}"
                                    )
                            }
                    )
            )
    })
    ApiResponse<ExternalResponseDTO.CreateResponseDTO> createExternal(
            @PathVariable("teamId") Long teamId,
            @Valid @RequestBody ExternalRequestDTO.ExternalCreateRequestDTO requestDTO,
            @AuthenticationPrincipal AuthUser user);

    @Operation(
            summary = "팀 내 외부이슈를 수정하는 API",
            description = "팀 내 외부이슈를 수정하는 API입니다. 담당자, 제목, 내용, 마감기한, 우선순위, 상태 값을 수정할 수 있습니다. " +
                    "수정할 데이터 값들을 요청 바디에 담아 보내주시면 됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "외부이슈 수정 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "유효성 검증 실패",
                                    value = "{\"isSuccess\": false, \"code\": \"COMMON400\", \"message\": \"잘못된 요청입니다.\", \"result\": null}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "외부이슈를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "외부이슈 없음",
                                    value = "{\"isSuccess\": false, \"code\": \"EXTERNAL400\", \"message\": \"해당하는 외부 이슈가 존재하지 않습니다.\", \"result\": null}"
                            )
                    )
            )
    })
    ApiResponse<ExternalResponseDTO.UpdateResponseDTO> modifyExternal(
            @Parameter(description = "외부이슈 ID", required = true) @PathVariable Long externalId,
            @Parameter(description = "팀 ID")@PathVariable Long teamId,
            @Valid @RequestBody ExternalRequestDTO.ExternalUpdateRequestDTO requestDTO);

    @Operation(
            summary = "팀 내 외부이슈들을 한번에 삭제하는 API",
            description = "외부이슈들의 식별자들을 리스트로 받아서 한번에 삭제처리하는 API입니다. 해당 외부이슈들은 실제 데이터베이스에서 삭제되는 것이 아닌 " +
                    "Soft Delete 방식으로, 휴지통으로 담기게 됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "외부이슈 삭제 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "유효성 검증 실패",
                                    value = "{\"isSuccess\": false, \"code\": \"COMMON400\", \"message\": \"잘못된 요청입니다.\", \"result\": null}"
                            )
                    )
            )
    })
    ApiResponse<?> deleteExternal(@Valid @RequestBody ExternalRequestDTO.ExternalDeleteRequestDTO requestDTO);

    @Operation(
            summary = "외부 이슈 작성 시 필요한 ID 조회",
            description = "외부 이슈 작성 시 필요한 ID를 가져옵니다. (팀명)-e(번호) 형식으로 구성됩니다."
    )
    ApiResponse<?> getExternalName(@PathVariable("teamId") Long teamId);

    @Operation(
            summary = "해당 팀과 연동된 모든 연동 툴 조회",
            description = "해당 팀과 연동되어 있는 모든 연동 툴 내역을 조회합니다."
    )
    ApiResponse<ExternalResponseDTO.LinkInfoResponseDTO> getExternalLinks(@PathVariable("teamId") Long teamId);

    @Operation(
            summary = "삭제된 외부이슈 복원 API",
            description = "삭제된 외부이슈를 복원합니다."
    )
    ApiResponse<List<ExternalResponseDTO.SimpleExternalDTO>> restoreGoals(
            @RequestBody ExternalRequestDTO.ExternalDeleteRequestDTO dto
    );

    @Operation(
            summary = "삭제된 외부이슈 목록 가져오기",
            description = "삭제된 모든 외부이슈들을 가져옵니다."
    )
    ApiResponse<List<ExternalResponseDTO.SimpleExternalDTO>> getDeletedExternals(@PathVariable("teamId") Long teamId);
}
