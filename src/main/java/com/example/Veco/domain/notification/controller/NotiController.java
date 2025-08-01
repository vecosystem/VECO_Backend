package com.example.Veco.domain.notification.controller;

import com.example.Veco.domain.notification.dto.NotiResDTO.*;
import com.example.Veco.domain.notification.exception.code.NotiSuccessCode;
import com.example.Veco.domain.notification.service.NotiQueryService;
import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.auth.user.AuthUser;
import com.example.Veco.global.enums.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            description = "마감기한이 오늘인 알림 목록을 조회합니다. Path Parameter로 알림타입 , Query String으로 필터종류 보내주시면 됩니다. " +
                    " 담당자가 본인인 task 중에 상태가 없음, 진행 중, 해야할 일 인 알림만 조회됩니다."
    )
    @GetMapping("{alarmtype}")
    public ApiResponse<GroupedNotiList<?>> getAlarmList(
            @AuthenticationPrincipal AuthUser user,
            @Parameter(description = "알림타입", required = true) @PathVariable("alarmtype") Category alarmtype,
            @Parameter(description = "필터 쿼리") @RequestParam(value = "query", required = false, defaultValue = "state") String query
    ){
        GroupedNotiList<?> result = notiQueryService.getNotiList(user,alarmtype,query);
        if (result != null){
            return ApiResponse.onSuccess(NotiSuccessCode.OK, result);
        } else {
            return ApiResponse.onSuccess(NotiSuccessCode.NO_CONTENT, null);
        }
    }

}
