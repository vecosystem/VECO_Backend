package com.example.Veco.domain.issue.service.command;

import com.example.Veco.domain.issue.dto.IssueReqDTO;
import com.example.Veco.domain.issue.dto.IssueResponseDTO;
import com.example.Veco.global.auth.user.AuthUser;

import java.util.List;

public interface IssueCommandService {
    IssueResponseDTO.UpdateIssue updateIssue(AuthUser user, IssueReqDTO.UpdateIssue dto, Long teamId, Long issueId);
    List<Long> deleteIssue( AuthUser user, Long teamId, IssueReqDTO.DeleteIssue dto);
}
