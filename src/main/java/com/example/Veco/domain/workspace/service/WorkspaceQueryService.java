package com.example.Veco.domain.workspace.service;


import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.workspace.entity.WorkSpace;

public interface WorkspaceQueryService {

    public WorkSpace getWorkSpaceByMember(Member member);
}
