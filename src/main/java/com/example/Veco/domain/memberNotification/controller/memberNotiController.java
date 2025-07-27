package com.example.Veco.domain.memberNotification.controller;

import com.example.Veco.domain.memberNotification.exception.code.MemberNotiSuccessCode;
import com.example.Veco.domain.memberNotification.service.MemberNotiCommandService;
import com.example.Veco.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    @PatchMapping("{memberId}/{alarmId}")
    public ApiResponse<Void> getAlarmList(
            @PathVariable Long memberId,  // HACK: memberId 임시 사용
            @PathVariable Long alarmId
    ){
        memberNotiCommandService.markAsRead(memberId, alarmId);
        return ApiResponse.onSuccess(MemberNotiSuccessCode.UPDATE,null);
    }

    @Operation(
            summary = " 알림 삭제 API ",
            description = "해당 알림을 삭제하는 API 입니다." +
                    "Request Body로 삭제할 알림ID 목록들을 주시면 됩니다."
    )
    @DeleteMapping("{memberId}")
    public ApiResponse<Void> deleteAlarms(
            @PathVariable Long memberId,   // HACK
            @RequestBody List<Long> alarmIds
    ) {
        memberNotiCommandService.deleteMemberNotifications(memberId, alarmIds);
        return ApiResponse.onSuccess(MemberNotiSuccessCode.DELETE,null);
    }

}
