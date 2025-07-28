package com.example.Veco.domain.external.converter;

import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.external.dto.request.ExternalRequestDTO;
import com.example.Veco.domain.external.dto.response.ExternalResponseDTO;
import com.example.Veco.domain.external.dto.GitHubWebhookPayload;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.mapping.Assignment;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.global.enums.ExtServiceType;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;

import java.util.ArrayList;
import java.util.List;

public class ExternalConverter {
    public static External toExternal(Team team, Goal goal, ExternalRequestDTO.ExternalCreateRequestDTO dto, String externalCode){
        External external = External.builder()
                .description(dto.getContent())
                .startDate(dto.getDeadline().getStart())
                .endDate(dto.getDeadline().getEnd())
                .external_code(externalCode)
                .description(dto.getDescription())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .externalCode(externalCode)
                .type(dto.getExtServiceType())
                .priority(dto.getPriority())
                .title(dto.getTitle())
                .state(dto.getState())
                .build();

        external.setTeam(team);
        external.setGoal(goal);

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

        comments.forEach(comment -> {

            ExternalResponseDTO.CommentResponseDTO commentDTO = ExternalResponseDTO.CommentResponseDTO.builder()
                    .profileUrl(comment.getMember().getProfile().getProfileImageUrl())
                    .nickname(comment.getMember().getNickname())
                    .createdAt(comment.getCreatedAt())
                    .content(comment.getContent())
                    .build();

            commentResponseDTOS.add(commentDTO);
        });

        ExternalResponseDTO.ExternalCommentResponseDTO commentResponseDTO = ExternalResponseDTO.ExternalCommentResponseDTO.builder()
                .cnt(commentResponseDTOS.size())
                .info(commentResponseDTOS)
                .build();

        List<ExternalResponseDTO.AssigneeInfoDTO> assigneeResponseDTOS = new ArrayList<>();

        assignees.forEach(assignee -> {
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

        return ExternalResponseDTO.ExternalInfoDTO.builder()
                .id(external.getId())
                .description(external.getDescription())
                .startDate(external.getStartDate())
                .endDate(external.getEndDate())
                .externalCode(external.getExternalCode())
                .content(external.getDescription())
                .name(external.getExternal_code())
                .priority(external.getPriority())
                .goalId(external.getGoal().getId())
                .goalTitle(external.getGoal().getTitle())
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
                .name(external.getExternal_code())
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
                .externalCode(externalCode)
                .name("GitHub Issue")
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
}
