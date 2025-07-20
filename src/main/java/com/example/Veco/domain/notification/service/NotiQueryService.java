package com.example.Veco.domain.notification.service;

import com.example.Veco.global.enums.Category;

public interface NotiQueryService {
    Object getNotiList(Long memberId, Category alarmType);
}
