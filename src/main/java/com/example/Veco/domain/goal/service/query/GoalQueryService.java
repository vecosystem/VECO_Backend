package com.example.Veco.domain.goal.service.query;

import com.example.Veco.domain.goal.converter.GoalConverter;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.Data;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.Teammate;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.mapping.MemberTeam;
import com.example.Veco.domain.mapping.MemberTeamRepository;
import com.example.Veco.domain.team.entity.TeamException;
import com.example.Veco.domain.team.entity.TeamRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalQueryService {

    // 리포지토리
    private final GoalRepository goalRepository;
    private final MemberTeamRepository memberTeamRepository;

    // 팀 내 모든 목표 조회
    // 목표 상세 조회
    // 팀원 조회
    public Data<Teammate> getTeammate(
            Long teamId
    ){
        // 팀원 조회
        List<MemberTeam> memberTeam = memberTeamRepository.findAllByTeamId(teamId);

        // 존재하면 DTO 담아 반환
        if (!memberTeam.isEmpty()){
            // 조회한 팀원 DTO 변환
            List<Teammate> teammateList = memberTeam.stream()
                    .map(GoalConverter::toTeammate)
                    .toList();
            // 응답 DTO 변환
            return GoalConverter.toData(teammateList);
        } else {
            return null;
        }
    }
}
