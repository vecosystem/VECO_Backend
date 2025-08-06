package com.example.Veco.domain.team.controller;

import com.example.Veco.domain.team.dto.NumberSequenceResponseDTO;
import com.example.Veco.domain.team.service.NumberSequenceService;
import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/{teamId}")
public class NumberSequenceController {
    private final NumberSequenceService numberSequenceService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<NumberSequenceResponseDTO>> getNextCode(@PathVariable("teamId") Long teamId,
                                                                              @RequestParam("category") Category category,
                                                                              @RequestParam("workspaceName") String workspaceName) {
        NumberSequenceResponseDTO numberSequenceResponseDTO = numberSequenceService.reserveNextNumber(workspaceName, teamId, category);
        return ResponseEntity.ok(ApiResponse.onSuccess(numberSequenceResponseDTO));
    }
}
