package com.example.Veco.domain.reminder.service;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.assignee.repository.AssigneeRepository;
import com.example.Veco.domain.mapping.entity.MemberTeam;
import com.example.Veco.domain.mapping.repository.MemberTeamRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.notification.service.NotiCommandService;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.global.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderService {

    // 사용자 GET 요청 시, 캐시된 todayTypeIds에 대한 알림 생성

    private final ReminderCacheService reminderCacheService;
    private final NotiCommandService notiCommandService;

    private final AssigneeRepository assigneeRepository;
    private final MemberTeamRepository memberTeamRepository;

    public void handleReminder(Member member) {

        Long memberId = member.getId(); // HACK
        List<MemberTeam> memberTeams = memberTeamRepository.findByMemberId(memberId);
        if (memberTeams.isEmpty()) return;


        for (Category type : Category.values()) {

            List<Long> todayTypeIds = reminderCacheService.getTodayTypeIds(type);
            if (todayTypeIds == null || todayTypeIds.isEmpty()) continue;

            List<Assignee> assignees = assigneeRepository
                    .findByMemberTeamsAndTypeAndTargetIds(memberTeams, type, todayTypeIds);

            for (Assignee assignee : assignees) {
                Long targetId = assignee.getTargetId();
                Team team = assignee.getMemberTeam().getTeam();
                notiCommandService.createNotification(type, targetId, team, member);
            }
        }
    }
}
