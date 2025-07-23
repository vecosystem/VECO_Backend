package com.example.Veco.domain.workspace.service;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import com.example.Veco.domain.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkspaceQueryServiceImpl implements WorkspaceQueryService {

    WorkspaceRepository workspaceRepository;

    public WorkSpace getWorkSpaceByMember(Member member) {
        return member.getWorkSpace();
    }
}
