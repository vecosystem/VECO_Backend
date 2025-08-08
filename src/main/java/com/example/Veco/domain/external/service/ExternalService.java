package com.example.Veco.domain.external.service;

import com.example.Veco.domain.assignee.repository.AssigneeRepository;
import com.example.Veco.domain.comment.entity.CommentRoom;
import com.example.Veco.domain.external.converter.ExternalConverter;
import com.example.Veco.domain.external.dto.request.ExternalRequestDTO;
import com.example.Veco.domain.external.dto.response.ExternalResponseDTO;
import com.example.Veco.domain.external.dto.response.ExternalGroupedResponseDTO;
import com.example.Veco.domain.external.dto.paging.ExternalSearchCriteria;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.external.repository.ExternalCustomRepository;
import com.example.Veco.domain.external.repository.ExternalRepository;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.mapping.Assignment;
import com.example.Veco.domain.mapping.converter.AssignmentConverter;
import com.example.Veco.domain.mapping.entity.MemberTeam;
import com.example.Veco.domain.mapping.repository.*;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.error.MemberErrorStatus;
import com.example.Veco.domain.member.error.MemberHandler;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.slack.util.SlackUtil;
import com.example.Veco.domain.team.dto.NumberSequenceResponseDTO;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.exception.TeamException;
import com.example.Veco.domain.team.exception.code.TeamErrorCode;
import com.example.Veco.domain.team.repository.TeamRepository;
import com.example.Veco.domain.team.service.NumberSequenceService;
import com.example.Veco.global.apiPayload.code.ErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;
import com.example.Veco.global.apiPayload.page.CursorPage;
import com.example.Veco.global.auth.user.AuthUser;
import com.example.Veco.global.enums.Category;
import com.example.Veco.global.enums.ExtServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExternalService {

    private final ExternalRepository externalRepository;
    private final NumberSequenceService numberSequenceService;
    private final AssigmentRepository assigmentRepository;
    private final ExternalCustomRepository externalCustomRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final TeamRepository teamRepository;
    private final GoalRepository goalRepository;
    private final MemberRepository memberRepository;
    private final LinkRepository linkRepository;
    private final AssigneeRepository assigneeRepository;

    // 유틸
    private final SlackUtil slackUtil;
    private final CommentRoomRepository commentRoomRepository;
    private final GitHubIssueService gitHubIssueService;
    private final GitHubInstallationRepository githubInstallationRepository;

    @Transactional
    public ExternalResponseDTO.CreateResponseDTO createExternal(Long teamId,
                                                                ExternalRequestDTO.ExternalCreateRequestDTO request,
                                                                AuthUser user) {

        Member author = memberRepository.findBySocialUid(user.getSocialUid())
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TeamErrorCode._NOT_FOUND));

        NumberSequenceResponseDTO sequenceDTO = numberSequenceService
                .allocateNextNumber(team.getWorkSpace().getName(), teamId, Category.EXTERNAL);


        List<Member> members = memberRepository.findAllByIdIn(request.getManagersId());

        if(request.getManagersId().size() != members.size()){
            throw new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND);
        }

        List<MemberTeam> memberTeamList = memberTeamRepository.findAllByMemberIdInAndTeamId(request.getManagersId(), teamId);
        if (memberTeamList.size() != request.getManagersId().size()) {
            throw new MemberHandler(MemberErrorStatus._FORBIDDEN);
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

        externalRepository.save(external);

        if(request.getExtServiceType() == ExtServiceType.GITHUB){
            gitHubIssueService.createGitHubIssue(request);
        }

        return ExternalConverter.createResponseDTO(external);
    }

    public ExternalResponseDTO.ExternalInfoDTO getExternalById(Long externalId) {

        CommentRoom commentRoom = commentRoomRepository
                .findByRoomTypeAndTargetId(com.example.Veco.global.enums.Category.EXTERNAL, externalId);

        External external = findExternalById(externalId);

        return ExternalConverter.toExternalInfoDTO(external, external.getAssignments(),
                commentRoom != null ? commentRoom.getComments() : null);
    }

    public CursorPage<ExternalResponseDTO.ExternalDTO> getExternalsWithPagination(ExternalSearchCriteria criteria, String cursor, int size){
        return externalCustomRepository.findExternalWithCursor(criteria, cursor, size);
    }

    public ExternalGroupedResponseDTO.ExternalGroupedPageResponse getExternalsWithGroupedPagination(ExternalSearchCriteria criteria, String cursor, int size){
        return ((com.example.Veco.domain.external.repository.ExternalCursorRepository) externalCustomRepository)
                .findExternalWithGroupedResponse(criteria, cursor, size);
    }

    public ExternalResponseDTO.SimpleListDTO getSimpleExternals(Long teamId) {
        return ExternalConverter.toSimpleListDTO(externalRepository.findByTeamId(teamId));
    }

    @Transactional
    public void deleteExternals(ExternalRequestDTO.ExternalDeleteRequestDTO request) {
        externalRepository.findByIdIn(request.getExternalIds());
    }

    @Transactional
    public void softDeleteExternals(ExternalRequestDTO.ExternalDeleteRequestDTO request) {
        List<External> externals = externalRepository.findByIdIn(request.getExternalIds());

        externals.forEach(External::softDelete);
    }

    @Transactional
    public ExternalResponseDTO.UpdateResponseDTO updateExternal(Long externalId, ExternalRequestDTO.ExternalUpdateRequestDTO request) {
        External external = findExternalById(externalId);

        if (request.getManagersId() != null) {
            modifyAssignment(external, request);
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

    public ExternalResponseDTO.LinkInfoResponseDTO getExternalServices(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TeamErrorCode._NOT_FOUND));

        Boolean isLinkedWithSlack = linkRepository.findByWorkspace(team.getWorkSpace()).isPresent();

        Boolean isLinkedWithGitHub = githubInstallationRepository.findByTeamId(teamId).isPresent();

        return ExternalConverter.linkInfoResponseDTO(isLinkedWithGitHub, isLinkedWithSlack);
    }

    private void modifyAssignment(External external, ExternalRequestDTO.ExternalUpdateRequestDTO request) {
        assigmentRepository.deleteByExternalId(external.getId());

        List<Member> members = memberRepository.findAllByIdIn(request.getManagersId());

        members.forEach(member -> {
            Assignment assignment = Assignment.builder()
                    .external(external)
                    .assigneeName(member.getName())
                    .category(Category.EXTERNAL)
                    .profileUrl(member.getProfile().getProfileImageUrl())
                    .assignee(member)
                    .build();

            external.addAssignment(assignment); // 양방향 연관관계 매핑
        });

        externalRepository.save(external);
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
