package com.example.Veco.domain.workspace.util;

import com.example.Veco.domain.workspace.repository.WorkspaceRepository;
import org.springframework.stereotype.Component;

@Component
public class SlugGenerator {

    private final WorkspaceRepository workspaceRepository;

    public SlugGenerator(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    public String generate(String name) {
        String baseSlug = name.toLowerCase().replaceAll("\\s+", "-");
        String slug = baseSlug;
        int suffix = 1;

        while (workspaceRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + suffix++;
        }

        return slug;
    }
}
