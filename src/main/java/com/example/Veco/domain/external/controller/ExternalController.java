package com.example.Veco.domain.external.controller;

import com.example.Veco.domain.external.dto.paging.ExternalSearchCriteria;
import com.example.Veco.domain.external.dto.request.ExternalRequestDTO;
import com.example.Veco.domain.external.dto.response.ExternalResponseDTO;
import com.example.Veco.domain.external.dto.response.ExternalGroupedResponseDTO;
import com.example.Veco.domain.external.exception.code.ExternalSuccessCode;
import com.example.Veco.domain.external.service.ExternalService;
import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.auth.user.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/{teamId}/externals")
@Tag(name = "외부이슈 API")
public class ExternalController implements ExternalSwaggerDocs{

    private final ExternalService externalService;

    @GetMapping("/externals-simple")
    public ApiResponse<ExternalResponseDTO.SimpleListDTO> getSimpleExternals(@PathVariable Long teamId) {
        return ApiResponse.onSuccess(externalService.getSimpleExternals(teamId));
    }

    @GetMapping("/{externalId}")
    public ApiResponse<ExternalResponseDTO.ExternalInfoDTO> getExternal(@PathVariable Long externalId) {
        return ApiResponse.onSuccess(externalService.getExternalById(externalId));
    }


    @GetMapping
    public ApiResponse<ExternalGroupedResponseDTO.ExternalGroupedPageResponse> getExternals(
            @PathVariable("teamId") Long teamId,
            @RequestParam(value = "query", defaultValue = "STATE") ExternalRequestDTO.ExternalGroupedSearchRequestDTO.FilterType query,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "size", defaultValue = "50") Integer size) {

        ExternalSearchCriteria searchCriteria = ExternalSearchCriteria.builder()
                .teamId(teamId)
                .filterType(query)
                .build();

        return ApiResponse.onSuccess(externalService.getExternalsWithGroupedPagination(searchCriteria, cursor, size));

    }

    // TODO : 깃허브 REST API 를 통해서 깃허브 레포지토리에도 이슈가 등록되도록! -> 양방향 동기화

    @PostMapping
    public ApiResponse<ExternalResponseDTO.CreateResponseDTO> createExternal(
            @PathVariable("teamId") Long teamId,
            @Valid @RequestBody ExternalRequestDTO.ExternalCreateRequestDTO requestDTO,
            @AuthenticationPrincipal AuthUser user) {

        return ApiResponse.onSuccess(externalService.createExternal(teamId, requestDTO, user));
    }

    @PatchMapping("/{externalId}")
    public ApiResponse<ExternalResponseDTO.UpdateResponseDTO> modifyExternal(
            @PathVariable Long externalId,
            @Valid @RequestBody ExternalRequestDTO.ExternalUpdateRequestDTO requestDTO) {

        return ApiResponse.onSuccess(externalService.updateExternal(externalId, requestDTO));
    }

    @DeleteMapping
    public ApiResponse<?> deleteExternal(@Valid @RequestBody ExternalRequestDTO.ExternalDeleteRequestDTO requestDTO) {
        externalService.deleteExternals(requestDTO);
        return ApiResponse.onSuccess(ExternalSuccessCode.DELETE);
    }

    @GetMapping("/external-name")
    public ApiResponse<?> getExternalName(@PathVariable("teamId") Long teamId) {
        return ApiResponse.onSuccess(externalService.getExternalName(teamId));
    }

    @GetMapping("/links")
    public ApiResponse<ExternalResponseDTO.LinkInfoResponseDTO> getExternalLinks(@PathVariable("teamId") Long teamId) {
        return ApiResponse.onSuccess(externalService.getExternalServices(teamId));
    }
}
