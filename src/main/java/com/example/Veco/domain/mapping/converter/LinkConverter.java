package com.example.Veco.domain.mapping.converter;

import com.example.Veco.domain.external.entity.ExternalService;
import com.example.Veco.domain.mapping.entity.Link;
import com.example.Veco.domain.workspace.entity.WorkSpace;

import java.time.LocalDateTime;

public class LinkConverter {

    // LinkedAt, Workspace, ExternalService -> Link (Slack)
    public static Link toLink(
            LocalDateTime linkedAt,
            WorkSpace workspace,
            ExternalService externalService
    ){
        return Link.builder()
                .linkedAt(linkedAt)
                .externalService(externalService)
                .workspace(workspace)
                .build();
    }
}
