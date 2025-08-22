package com.example.Veco.domain.external.service;


import com.example.Veco.domain.external.converter.ExternalConverter;
import com.example.Veco.domain.external.dto.request.ExternalRequestDTO;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.external.repository.ExternalRepository;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.mapping.Assignment;
import com.example.Veco.domain.mapping.converter.AssignmentConverter;
import com.example.Veco.domain.mapping.entity.MemberTeam;
import com.example.Veco.domain.mapping.repository.MemberTeamRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.error.MemberErrorStatus;
import com.example.Veco.domain.member.error.MemberHandler;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.team.dto.NumberSequenceResponseDTO;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.exception.TeamException;
import com.example.Veco.domain.team.exception.code.TeamErrorCode;
import com.example.Veco.domain.team.repository.TeamRepository;
import com.example.Veco.domain.team.service.NumberSequenceService;
import com.example.Veco.global.apiPayload.code.ErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;
import com.example.Veco.global.auth.user.AuthUser;
import com.example.Veco.global.enums.Category;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExternalTransactionalService {

    // 리포지토리
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final ExternalRepository externalRepository;
    private final NumberSequenceService numberSequenceService;
    private final MemberTeamRepository memberTeamRepository;
    private final GoalRepository goalRepository;

    // 외부 이슈 생성
    @Transactional
    public External createExternal(
            AuthUser user,
            Long teamId,
            ExternalRequestDTO.ExternalCreateRequestDTO request
    ) {

        Member author = memberRepository.findBySocialUid(user.getSocialUid())
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TeamErrorCode._NOT_FOUND));

        NumberSequenceResponseDTO sequenceDTO = numberSequenceService
                .allocateNextNumber(team.getWorkSpace().getName(), teamId, Category.EXTERNAL);


        List<Member> members = memberRepository.findAllByIdIn(request.getManagersId());

        if (request.getManagersId() != null) {
            if(request.getManagersId().size() != members.size()){
                throw new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND);
            }

            List<MemberTeam> memberTeamList = memberTeamRepository.findAllByMemberIdInAndTeamId(request.getManagersId(), teamId);
            if (memberTeamList.size() != request.getManagersId().size()) {
                throw new MemberHandler(MemberErrorStatus._FORBIDDEN);
            }
        }

        Goal goal = null;

        if(request.getGoalId() != null){
            goal = findGoalById(request.getGoalId());
        }

        External external = ExternalConverter.toExternal(team, goal, request, sequenceDTO.getNextCode(), author);

        members.forEach(member -> {
            Assignment assignment = AssignmentConverter.toAssignment(member, external, Category.EXTERNAL);
            external.addAssignment(assignment);
        });

        return externalRepository.save(external);
    }

    private Goal findGoalById(Long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new VecoException(ErrorStatus.GOAL_NOT_FOUND));
    }
}
