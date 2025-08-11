package com.example.Veco.domain.issue.dto;

import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;

import java.time.LocalDate;
import java.util.List;

public class IssueReqDTO {

    public record CreateIssue (
            String title,
            String content,
            State state,
            Priority priority,
            List<Long> managersId,
            Deadline deadline,
            Long goalId
    ){}

    public record UpdateIssue(
            String title,
            String content,
            State state,
            Priority priority,
            List<Long> managersId,
            Deadline deadline,
            Long goalId
    ){}

    public record Deadline (
            LocalDate start,
            LocalDate end
    ){}

    public record DeleteIssue(
            List<Long> issueIds
    ){}
}
