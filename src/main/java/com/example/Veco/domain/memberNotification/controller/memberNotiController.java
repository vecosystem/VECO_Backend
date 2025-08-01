package com.example.Veco.domain.memberNotification.controller;

import com.example.Veco.domain.memberNotification.exception.code.MemberNotiSuccessCode;
import com.example.Veco.domain.memberNotification.service.MemberNotiCommandService;
import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.auth.user.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms")
@Tag(name = "멤버알림 API")
public class memberNotiController {

    private final MemberNotiCommandService memberNotiCommandService;

    @Operation(
            summary = " 알림 읽음처리 API ",
            description = "알림 읽음여부를 TRUE로 변경하는 API입니다." +
                    " Path Parameter로 알림ID 주시면 됩니다."
    )
    @PatchMapping("{alarmId}")
    public ApiResponse<Void> getAlarmList(
            @AuthenticationPrincipal AuthUser user,
            @Parameter(description = "알림ID", required = true) @PathVariable("alarmId") Long alarmId
    ){
        memberNotiCommandService.markAsRead(user, alarmId);
        return ApiResponse.onSuccess(MemberNotiSuccessCode.UPDATE,null);
    }

    @Operation(
            summary = " 알림 삭제 API ",
            description = "해당 알림을 삭제하는 API 입니다." +
                    "Request Body로 삭제할 알림ID 목록들을 주시면 됩니다."
    )
    @DeleteMapping("")
    public ApiResponse<Void> deleteAlarms(
            @AuthenticationPrincipal AuthUser user,
            @Parameter(description = "알림ID 리스트", required = true) @RequestBody List<Long> alarmIds
    ) {
        memberNotiCommandService.deleteMemberNotifications(user, alarmIds);
        return ApiResponse.onSuccess(MemberNotiSuccessCode.DELETE,null);
    }

}
