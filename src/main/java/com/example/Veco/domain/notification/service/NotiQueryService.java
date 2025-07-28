package com.example.Veco.domain.notification.service;

import com.example.Veco.domain.notification.dto.NotiResDTO.*;
import com.example.Veco.global.enums.Category;

public interface NotiQueryService {
    GroupedNotiList getNotiList(Long memberId, Category alarmType, String query);
}
