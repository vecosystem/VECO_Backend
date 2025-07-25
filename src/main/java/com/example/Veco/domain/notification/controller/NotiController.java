package com.example.Veco.domain.notification.controller;

import com.example.Veco.domain.notification.service.NotiQueryService;
import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.enums.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms")
@Tag(name = "알림 API")
public class NotiController {

    private final NotiQueryService notiQueryService;

    // GET : 이슈/목표/외부 알림목록 조회
    @Operation(
            summary = " 목표/이슈/외부 리마인드 알림 목록 API ",
            description = "마감기한이 오늘까지인 알림 목록을 조회합니다. Path Parameter로 알림타입 (ISSUE,GOAL,EXTERNAL), Query String으로 필터종류 보내주시면 됩니다. " +
                    " 담당자가 본인인 task 중에 상태가 없음, 진행 중 인 알림만 조회됩니다."
    )
    @GetMapping("{memberId}/{alarmtype}")
    public ApiResponse<?> getAlarmList(
            @PathVariable Long memberId,  // HACK: memberId 임시 사용
            @PathVariable Category alarmtype,
            @RequestParam(required = false, defaultValue = "state") String query
    ){
        return ApiResponse.onSuccess(notiQueryService.getNotiList(memberId,alarmtype,query));
    }

}
