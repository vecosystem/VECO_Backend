package com.example.Veco.domain.external.service;

import com.example.Veco.domain.comment.entity.CommentRoom;
import com.example.Veco.domain.external.converter.ExternalConverter;
import com.example.Veco.domain.external.dto.request.ExternalRequestDTO;
import com.example.Veco.domain.external.dto.response.ExternalResponseDTO;
import com.example.Veco.domain.external.dto.paging.ExternalSearchCriteria;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.external.repository.ExternalCustomRepository;
import com.example.Veco.domain.external.repository.ExternalRepository;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.mapping.Assignment;
import com.example.Veco.domain.mapping.entity.Link;
import com.example.Veco.domain.mapping.repository.AssigmentRepository;
import com.example.Veco.domain.mapping.repository.CommentRoomRepository;
import com.example.Veco.domain.mapping.repository.LinkRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.slack.dto.SlackResDTO;
import com.example.Veco.domain.slack.exception.SlackException;
import com.example.Veco.domain.slack.exception.code.SlackErrorCode;
import com.example.Veco.domain.slack.util.SlackUtil;
import com.example.Veco.domain.team.converter.AssigneeConverter;
import com.example.Veco.domain.team.dto.AssigneeResponseDTO;
import com.example.Veco.domain.team.dto.NumberSequenceResponseDTO;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.enums.Category;
import com.example.Veco.domain.team.exception.TeamException;
import com.example.Veco.domain.team.exception.code.TeamErrorCode;
import com.example.Veco.domain.team.repository.TeamRepository;
import com.example.Veco.domain.team.service.NumberSequenceService;
import com.example.Veco.global.apiPayload.code.ErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;
import com.example.Veco.global.apiPayload.page.CursorPage;
import com.example.Veco.global.enums.ExtServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExternalService {

    private final ExternalRepository externalRepository;
    private final NumberSequenceService numberSequenceService;
    private final AssigmentRepository assigmentRepository;
    private final ExternalCustomRepository externalCustomRepository;
    private final TeamRepository teamRepository;
    private final GoalRepository goalRepository;
    private final MemberRepository memberRepository;
    private final LinkRepository linkRepository;

    // 유틸
    private final SlackUtil slackUtil;
    private final CommentRoomRepository commentRoomRepository;

    @Transactional
    public ExternalResponseDTO.CreateResponseDTO createExternal(Long teamId, ExternalRequestDTO.ExternalCreateRequestDTO request){

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TeamErrorCode._NOT_FOUND));

        NumberSequenceResponseDTO sequenceDTO = numberSequenceService
                .allocateNextNumber(team.getWorkSpace().getName(), teamId, Category.EXTERNAL);


        Goal goal = findGoalById(request.getGoalId());

        External external = ExternalConverter.toExternal(team, goal, request, sequenceDTO.getNextCode());

        externalRepository.save(external);

        return ExternalConverter.createResponseDTO(external);
    }

    public ExternalResponseDTO.ExternalInfoDTO getExternalById(Long externalId) {

        List<Assignment> assignments = assigmentRepository.findByExternalId(externalId);

        CommentRoom commentRoom = commentRoomRepository
                .findByRoomTypeAndTargetId(com.example.Veco.global.enums.Category.EXTERNAL, externalId);

        External external = findExternalById(externalId);

        return ExternalConverter.toExternalInfoDTO(external, external.getAssignments(), commentRoom.getComments());
    }

    public CursorPage<ExternalResponseDTO.ExternalDTO> getExternalsWithPagination(ExternalSearchCriteria criteria, String cursor, int size){
        return externalCustomRepository.findExternalWithCursor(criteria, cursor, size);
    }

    @Transactional
    public void deleteExternals(ExternalRequestDTO.ExternalDeleteRequestDTO request) {
        externalRepository.deleteAllById(request.getExternalIds());
    }

    @Transactional
    public ExternalResponseDTO.UpdateResponseDTO updateExternal(Long externalId, ExternalRequestDTO.ExternalUpdateRequestDTO request) {
        External external = findExternalById(externalId);

        if (request.getManagersId() != null) {
            modifyAssignment(externalId, request, external);
        }

        if(request.getGoalId() != null){
            Goal goal = findGoalById(request.getGoalId());

            external.setGoal(goal);
        }

        external.updateExternal(request);

        return ExternalConverter.updateResponseDTO(external);
    }

    public String getExternalName(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TeamErrorCode._NOT_FOUND));

        NumberSequenceResponseDTO numberSequenceResponseDTO =
                numberSequenceService.reserveNextNumber(team.getWorkSpace().getName(), teamId, Category.EXTERNAL);

        return numberSequenceResponseDTO.getNextCode();
    }

    private void modifyAssignment(Long externalId, ExternalRequestDTO.ExternalUpdateRequestDTO request, External external) {
        assigmentRepository.deleteByExternalId(externalId);

        List<Member> members = memberRepository.findAllByIdIn(request.getManagersId());

        List<Assignment> assignments = new ArrayList<>();

        members.forEach(member -> {
            Assignment assignment = Assignment.builder()
                    .external(external)
                    .assigneeName(member.getName())
                    .category(Category.EXTERNAL)
                    .profileUrl(member.getProfile().getProfileImageUrl())
                    .assignee(member)
                    .build();

            assignments.add(assignment);
        });

        assigmentRepository.saveAll(assignments);
    }

    private External findExternalById(Long externalId) {
        return externalRepository.findById(externalId)
                .orElseThrow(() -> new VecoException(ErrorStatus.EXTERNAL_NOT_FOUND));
    }

    private Goal findGoalById(Long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new VecoException(ErrorStatus.GOAL_NOT_FOUND));
    }
}
