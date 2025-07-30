package com.example.Veco.domain.external.converter;

import com.example.Veco.domain.external.dto.ExternalRequestDTO;
import com.example.Veco.domain.external.dto.ExternalResponseDTO;
import com.example.Veco.domain.external.dto.GitHubWebhookPayload;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.team.dto.AssigneeResponseDTO;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.global.enums.ExtServiceType;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;

import java.util.List;

public class ExternalConverter {
    public static External toExternal(Team team, Goal goal, ExternalRequestDTO.ExternalCreateRequestDTO dto, String externalCode){
        External external = External.builder()
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

    public static ExternalResponseDTO.ExternalDTO toExternalDTO(External external, List<AssigneeResponseDTO.AssigneeDTO> assignees){
        return ExternalResponseDTO.ExternalDTO.builder()
                .id(external.getId())
                .description(external.getDescription())
                .startDate(external.getStartDate())
                .endDate(external.getEndDate())
                .externalCode(external.getExternalCode())
                .priority(external.getPriority())
                .title(external.getTitle())
                .state(external.getState())
                .assignees(assignees)
                .extServiceType(external.getType())
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
}
