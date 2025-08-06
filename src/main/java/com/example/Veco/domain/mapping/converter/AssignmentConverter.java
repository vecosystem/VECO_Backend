package com.example.Veco.domain.mapping.converter;

import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.mapping.Assignment;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.global.enums.Category;

public class AssignmentConverter {
    public static Assignment toAssignment(Member member, External external, Category category) {
        return Assignment.builder()
                .assigneeName(member.getName())
                .profileUrl(member.getProfile().getProfileImageUrl())
                .category(category)
                .assignee(member)
                .external(external)
                .build();
    }
}
