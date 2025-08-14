package com.example.Veco.domain.external.converter;

import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.external.exception.ExternalException;
import com.example.Veco.domain.external.exception.code.ExternalErrorCode;
import com.example.Veco.domain.github.dto.webhook.GitHubPullRequestPayload;
import com.example.Veco.domain.external.dto.request.ExternalRequestDTO;
import com.example.Veco.domain.external.dto.response.ExternalResponseDTO;
import com.example.Veco.domain.external.dto.response.ExternalGroupedResponseDTO;
import com.example.Veco.domain.github.dto.webhook.GitHubWebhookPayload;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.exception.GoalException;
import com.example.Veco.domain.goal.exception.code.GoalErrorCode;
import com.example.Veco.domain.mapping.Assignment;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.global.enums.ExtServiceType;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ExternalConverter {

    public static ExternalResponseDTO.SimpleListDTO toSimpleListDTO(List<External> externals) {
        List<ExternalResponseDTO.SimpleExternalDTO> simpleDTOs = externals.stream().map(
                external -> ExternalResponseDTO.SimpleExternalDTO.builder()
                        .id(external.getId())
                        .title(external.getTitle())
                        .build()
        ).toList();

        return ExternalResponseDTO.SimpleListDTO.builder()
                .cnt(externals.size())
                .info(simpleDTOs)
                .build();
    }

    public static External toExternal(Team team, Goal goal,
                                      ExternalRequestDTO.ExternalCreateRequestDTO dto,
                                      String externalCode,
                                      Member author){

        LocalDate start = null;
        LocalDate end = null;
        try {
            if (dto.getDeadline().getStart() != null) {
                start = LocalDate.parse(dto.getDeadline().getStart());
            }
            if (dto.getDeadline().getEnd() != null) {
                end = LocalDate.parse(dto.getDeadline().getEnd());
            }
        } catch (DateTimeParseException e) {
            throw new ExternalException(ExternalErrorCode.DEADLINE_INVALID);
        }

        External external = External.builder()
                .member(author)
                .description(dto.getContent())
                .name(externalCode)
                .startDate(start)
                .endDate(end)
                .type(dto.getExtServiceType())
                .priority(dto.getPriority())
                .title(dto.getTitle())
                .state(dto.getState())
                .build();

        external.setTeam(team);

        if(goal != null){
            external.setGoal(goal);
        }

        return external;
    }

    public static ExternalResponseDTO.ExternalInfoDTO toExternalInfoDTO(External external,
                                                                        List<Assignment> assignees,
                                                                        List<Comment> comments){

        ExternalResponseDTO.DeadlineResponseDTO deadline = ExternalResponseDTO.DeadlineResponseDTO.builder()
                .start(external.getStartDate())
                .end(external.getEndDate())
                .build();

        List<ExternalResponseDTO.CommentResponseDTO> commentResponseDTOS = new ArrayList<>();

        if(comments != null){
            comments.forEach(comment -> {

                ExternalResponseDTO.CommentResponseDTO commentDTO = ExternalResponseDTO.CommentResponseDTO.builder()
                        .profileUrl(comment.getMember().getProfile().getProfileImageUrl())
                        .nickname(comment.getMember().getNickname())
                        .createdAt(comment.getCreatedAt())
                        .content(comment.getContent())
                        .build();

                commentResponseDTOS.add(commentDTO);
            });
        }

        ExternalResponseDTO.ExternalCommentResponseDTO commentResponseDTO = ExternalResponseDTO.ExternalCommentResponseDTO.builder()
                .cnt(commentResponseDTOS.size())
                .info(commentResponseDTOS)
                .build();

        List<ExternalResponseDTO.AssigneeInfoDTO> assigneeResponseDTOS = new ArrayList<>();

        if(assignees != null){
            assignees.forEach(assignee -> {
                ExternalResponseDTO.AssigneeInfoDTO assigneeInfoDTO = ExternalResponseDTO.AssigneeInfoDTO.builder()
                        .profileUrl(assignee.getProfileUrl())
                        .nickname(assignee.getAssigneeName())
                        .build();

                assigneeResponseDTOS.add(assigneeInfoDTO);
            });
        }

        ExternalResponseDTO.AssigneeResponseDTO assigneeResponseDTO = ExternalResponseDTO.AssigneeResponseDTO.builder()
                .cnt(assigneeResponseDTOS.size())
                .info(assigneeResponseDTOS)
                .build();

        return ExternalResponseDTO.ExternalInfoDTO.builder()
                .id(external.getId())
                .content(external.getDescription())
                .startDate(external.getStartDate())
                .endDate(external.getEndDate())
                .content(external.getDescription())
                .name(external.getName())
                .priority(external.getPriority())
                .goalId(external.getGoal()!= null ? external.getGoal().getId() : null)
                .goalTitle(external.getGoal()!= null ? external.getGoal().getTitle() : null)
                .deadlines(deadline)
                .title(external.getTitle())
                .state(external.getState())
                .comments(commentResponseDTO)
                .managers(assigneeResponseDTO)
                .extServiceType(external.getType())
                .build();
    }

    public static ExternalResponseDTO.ExternalDTO toExternalDTO(External external, List<Assignment> assignments) {

        ExternalResponseDTO.DeadlineResponseDTO deadline = ExternalResponseDTO.DeadlineResponseDTO.builder()
                .start(external.getStartDate())
                .end(external.getEndDate())
                .build();


        List<ExternalResponseDTO.AssigneeInfoDTO> assigneeResponseDTOS = new ArrayList<>();

        assignments.forEach(assignee -> {
            ExternalResponseDTO.AssigneeInfoDTO assigneeInfoDTO = ExternalResponseDTO.AssigneeInfoDTO.builder()
                    .profileUrl(assignee.getProfileUrl())
                    .nickname(assignee.getAssigneeName())
                    .build();

            assigneeResponseDTOS.add(assigneeInfoDTO);
        });

        ExternalResponseDTO.AssigneeResponseDTO assigneeResponseDTO = ExternalResponseDTO.AssigneeResponseDTO.builder()
                .cnt(assigneeResponseDTOS.size())
                .info(assigneeResponseDTOS)
                .build();

        return ExternalResponseDTO.ExternalDTO.builder()
                .id(external.getId())
                .content(external.getDescription())
                .name(external.getName())
                .priority(external.getPriority())
                .goalId(external.getGoal() != null ? external.getGoal().getId() : null)
                .goalTitle(external.getGoal() != null ? external.getGoal().getTitle() : null)
                .deadlines(deadline)
                .title(external.getTitle())
                .state(external.getState())
                .managers(assigneeResponseDTO)
                .build();

    }

    public static External byGitHubIssue(GitHubWebhookPayload payload, Team team, String externalCode){
        return External.builder()
                .title(payload.getIssue().getTitle())
                .githubDataId(payload.getIssue().getId())
                .description(payload.getIssue().getBody())
                .name(externalCode)
                .team(team)
                .type(ExtServiceType.GITHUB)
                .state(State.NONE)
                .priority(Priority.NONE)
                .build();
    }

    public static External byGitHubPullRequest(GitHubPullRequestPayload payload, Team team, String externalCode){

        log.info("GitHub pull request started");

        log.info("pr 제목 : {} pr 내용 : {}" , payload.getPullRequest().getTitle(), payload.getPullRequest().getBody());

        return External.builder()
                .title(payload.getPullRequest().getTitle())
                .githubDataId(payload.getPullRequest().getId())
                .description(payload.getPullRequest().getBody())
                .name(externalCode)
                .team(team)
                .type(ExtServiceType.GITHUB)
                .state(State.NONE)
                .priority(Priority.NONE)
                .build();
    }

    public static ExternalResponseDTO.UpdateResponseDTO updateResponseDTO(External external){
        return ExternalResponseDTO.UpdateResponseDTO.builder()
                .externalId(external.getId())
                .updatedAt(external.getUpdatedAt())
                .build();
    }

    public static ExternalResponseDTO.CreateResponseDTO createResponseDTO(External external){
        return ExternalResponseDTO.CreateResponseDTO.builder()
                .externalId(external.getId())
                .createdAt(external.getCreatedAt())
                .build();
    }

    public static ExternalGroupedResponseDTO.ExternalGroupedPageResponse toGroupedPageResponse(
            List<External> externals, 
            boolean hasNext, 
            String nextCursor, 
            int pageSize) {
        
        Map<State, List<External>> groupedByState = externals.stream()
                .collect(Collectors.groupingBy(External::getState));

        List<ExternalGroupedResponseDTO.FilteredExternalGroup> data = new ArrayList<>();
        
        for (State state : State.values()) {
            List<External> stateExternals = groupedByState.getOrDefault(state, new ArrayList<>());
            
            List<ExternalGroupedResponseDTO.ExternalItemDTO> externalItems = stateExternals.stream()
                    .map(ExternalConverter::toExternalItemDTO)
                    .collect(Collectors.toList());

            ExternalGroupedResponseDTO.FilteredExternalGroup group = ExternalGroupedResponseDTO.FilteredExternalGroup.builder()
                    .filterName(state.name())
                    .dataCnt(stateExternals.size())
                    .externals(externalItems)
                    .build();
            
            data.add(group);
        }

        return ExternalGroupedResponseDTO.ExternalGroupedPageResponse.builder()
                .data(data)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .pageSize(pageSize)
                .build();
    }

    public static ExternalGroupedResponseDTO.ExternalItemDTO toExternalItemDTO(External external) {
        ExternalGroupedResponseDTO.DeadlineDTO deadline = ExternalGroupedResponseDTO.DeadlineDTO.builder()
                .start(external.getStartDate() != null ? external.getStartDate().toString() : null)
                .end(external.getEndDate() != null ? external.getEndDate().toString() : null)
                .build();

        List<ExternalGroupedResponseDTO.ManagerInfoDTO> managerInfos = external.getAssignments().stream()
                .map(assignment -> ExternalGroupedResponseDTO.ManagerInfoDTO.builder()
                        .profileUrl(assignment.getProfileUrl())
                        .name(assignment.getAssigneeName())
                        .build())
                .collect(Collectors.toList());

        ExternalGroupedResponseDTO.ManagersDTO managers = ExternalGroupedResponseDTO.ManagersDTO.builder()
                .cnt(managerInfos.size())
                .info(managerInfos)
                .build();

        return ExternalGroupedResponseDTO.ExternalItemDTO.builder()
                .id(external.getId())
                .name(external.getName())
                .title(external.getTitle())
                .state(external.getState())
                .priority(external.getPriority().name())
                .deadline(deadline)
                .managers(managers)
                .extServiceType(external.getType())
                .build();
    }

    public static ExternalResponseDTO.LinkInfoResponseDTO linkInfoResponseDTO(Boolean linkedGitHub, Boolean linkedSlack) {
        return ExternalResponseDTO.LinkInfoResponseDTO.builder()
                .linkedWithGithub(linkedGitHub)
                .linkedWithSlack(linkedSlack)
                .build();
    }
}
