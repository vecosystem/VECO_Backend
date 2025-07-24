package com.example.Veco.domain.notification.service;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.global.enums.Category;

public interface NotiCommandService {
    void createNotification(Category type, Long typeId, Team team, Member member);
}
