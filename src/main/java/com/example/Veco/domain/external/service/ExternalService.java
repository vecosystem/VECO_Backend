package com.example.Veco.domain.external.service;

import com.example.Veco.domain.assignee.repository.AssigneeRepository;
import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.comment.entity.CommentRoom;
import com.example.Veco.domain.comment.repository.CommentRepository;
import com.example.Veco.domain.external.converter.ExternalConverter;
import com.example.Veco.domain.external.dto.paging.ExternalSearchCriteria;
import com.example.Veco.domain.external.dto.request.ExternalRequestDTO;
import com.example.Veco.domain.external.dto.response.ExternalGroupedResponseDTO;
import com.example.Veco.domain.external.dto.response.ExternalResponseDTO;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.external.exception.ExternalException;
import com.example.Veco.domain.external.exception.code.ExternalErrorCode;
import com.example.Veco.domain.external.repository.ExternalCustomRepository;
import com.example.Veco.domain.external.repository.ExternalRepository;
import com.example.Veco.domain.github.service.GitHubIssueService;
import com.example.Veco.domain.goal.converter.GoalConverter;
import com.example.Veco.domain.goal.dto.request.GoalReqDTO;
import com.example.Veco.domain.goal.dto.response.GoalResDTO;
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
import com.example.Veco.domain.slack.exception.SlackException;
import com.example.Veco.domain.slack.exception.code.SlackErrorCode;
import com.example.Veco.domain.slack.util.SlackUtil;
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
import com.example.Veco.global.enums.ExtServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final CommentRepository commentRepository;

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

        externalRepository.save(external);

        if(request.getExtServiceType() == ExtServiceType.GITHUB){
            gitHubIssueService.createGitHubIssue(request);
        } else if (request.getExtServiceType() == ExtServiceType.SLACK){
            // accessToken, DefaultChannelId, message
            // 연동 정보 조회
            com.example.Veco.domain.external.entity.ExternalService externalService =
                    linkRepository.findLinkByWorkspaceAndExternalService_ServiceType(
                    team.getWorkSpace(), ExtServiceType.SLACK)
                    .orElseThrow(() -> new SlackException(SlackErrorCode.NOT_LINKED))
                    .getExternalService();

            String message = team.getName() + "에서 " +
                    external.getTitle() + "을(를) 생성했습니다.";

            slackUtil.PostSlackMessage(
                    externalService.getAccessToken(),
                    externalService.getSlackDefaultChannelId(),
                    message
            );
        }

        return ExternalConverter.createResponseDTO(external);
    }

    public ExternalResponseDTO.ExternalInfoDTO getExternalById(Long externalId, Long teamId) {

        External external = findExternalById(externalId);

        CommentRoom commentRoom = commentRoomRepository
                .findByRoomTypeAndTargetId(com.example.Veco.global.enums.Category.EXTERNAL, externalId);

        if(!external.getTeam().getId().equals(teamId)){
            throw new ExternalException(ExternalErrorCode.NOT_SAME_TEAM);
        }

        List<Comment> comments = new ArrayList<>();

        if (commentRoom != null) {
            comments = commentRepository.findAllByCommentRoomOrderByIdAsc(commentRoom);
        }


        return ExternalConverter.toExternalInfoDTO(external, external.getAssignments(), comments);
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

        log.info("Delete external {}", request.getExternalIds());

        List<External> externals = externalRepository.findByIdIn(request.getExternalIds());

        externals.forEach(External::softDelete);
    }

    // 삭제된 목표 리스트 조회
    public List<ExternalResponseDTO.SimpleExternalDTO> getDeletedExternals(
            Long teamId
    ) {

        List<External> result = externalRepository.findAllByTeamIdAndDeleted(teamId);

        if (result.isEmpty()){
            throw new ExternalException(ExternalErrorCode.NOT_FOUND_DELETE_EXTERNALS);
        }

        return result.stream().map(ExternalConverter::toSimpleExternalDTO).toList();
    }


    @Transactional
    public List<ExternalResponseDTO.SimpleExternalDTO> restoreGoals(
            ExternalRequestDTO.ExternalDeleteRequestDTO dto
    ) {
        // 삭제된 목표들 조회
        List<External> result = externalRepository.findDeletedExternalsById(dto.getExternalIds());

        if (result.isEmpty()){
            throw new ExternalException(ExternalErrorCode.NOT_A_DELETE);
        }

        for (External external : result) {
            external.restore();
        }

        return result.stream().map(ExternalConverter::toSimpleExternalDTO).collect(Collectors.toList());
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

        if(request.getDeadline() != null){
            updateExternalDates(external, request.getDeadline());
        }

        external.updateExternal(request);

        return ExternalConverter.updateResponseDTO(external);
    }

    private void updateExternalDates(External external, ExternalRequestDTO.DeadlineRequestDTO deadline) {

        // 기한 변경
        if (deadline != null) {

            try {
                if (deadline.getStart() != null) {
                    LocalDate start;
                    if (deadline.getStart().equals("null")){
                        start = null;
                    } else {
                        start = LocalDate.parse(deadline.getStart());
                    }
                    external.updateStartDate(start);
                }
                if (deadline.getEnd() != null) {
                    LocalDate end;
                    if (deadline.getEnd().equals("null")){
                        end = null;
                    } else {
                        end = LocalDate.parse(deadline.getEnd());
                    }
                    external.updateEndDate(end);
                }
            } catch (DateTimeParseException e) {
                throw new ExternalException(ExternalErrorCode.DEADLINE_INVALID);
            }

        }

        validateDates(external.getStartDate(), external.getEndDate());
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 마감일보다 늦을 수 없습니다.");
        }
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
