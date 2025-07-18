package com.example.Veco.domain.external.controller;

import com.example.Veco.domain.external.dto.ExternalRequestDTO;
import com.example.Veco.domain.external.dto.ExternalResponseDTO;
import com.example.Veco.domain.external.dto.ExternalSearchCriteria;
import com.example.Veco.domain.external.service.ExternalService;
import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.apiPayload.page.CursorPage;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/externals")
public class ExternalController {

    private final ExternalService externalService;

    // TODO : 깃허브 REST API 를 통해서 깃허브 레포지토리에도 이슈가 등록되도록! -> 양방향 동기화
    @PostMapping("/")
    public ResponseEntity<ApiResponse<?>> createExternal(@RequestBody ExternalRequestDTO.ExternalCreateRequestDTO requestDTO) {
        Long externalId = externalService.createExternal(requestDTO);

        return ResponseEntity.ok(ApiResponse.onSuccess(externalId));
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<CursorPage<ExternalResponseDTO.ExternalDTO>>> getExternals(@RequestParam("state")State state,
                                                                                     @RequestParam("priority")Priority priority,
                                                                                     @RequestParam("assigneeId") Long assigneeId,
                                                                                     @RequestParam("cursor") String cursor,
                                                                                     @RequestParam("size") Integer size) {

        ExternalSearchCriteria searchCriteria = ExternalSearchCriteria.builder()
                .state(state)
                .priority(priority)
                .assigneeId(assigneeId).build();

        return ResponseEntity.ok(ApiResponse.onSuccess(externalService.getExternalsWithPagenation(searchCriteria, cursor, size)));

    }

    @GetMapping("/{externalId}")
    public ResponseEntity<ApiResponse<ExternalResponseDTO.ExternalDTO>> getExternal(@PathVariable Long externalId) {
        return ResponseEntity.ok(ApiResponse.onSuccess(externalService.getExternalById(externalId)));
    }

    @DeleteMapping("/")
    public ResponseEntity<ApiResponse<?>> deleteExternal(@RequestBody ExternalRequestDTO.ExternalDeleteRequestDTO requestDTO) {
        externalService.deleteExternals(requestDTO);
        return ResponseEntity.ok(ApiResponse.onSuccessWithNullResult());
    }

    @PatchMapping("/{externalId}")
    public ResponseEntity<ApiResponse<?>> modifyExternal(@PathVariable Long externalId,
                                                         @RequestBody ExternalRequestDTO.ExternalUpdateRequestDTO requestDTO) {
        externalService.updateExternal(externalId, requestDTO);
        return ResponseEntity.ok(ApiResponse.onSuccessWithNullResult());
    }
}
