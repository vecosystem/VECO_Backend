package com.example.Veco.domain.external.service;

import com.example.Veco.domain.external.dto.request.ExternalRequestDTO;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.external.repository.ExternalRepository;
import com.example.Veco.domain.mapping.Assignment;
import com.example.Veco.domain.mapping.repository.AssigmentRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.profile.entity.Profile;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
class ExternalServiceTest {

    @Autowired
    private ExternalService externalService;

    @Autowired
    private ExternalRepository externalRepository;

    @Autowired
    private AssigmentRepository assigmentRepository;

    @Autowired
    private MemberRepository memberRepository;


    @BeforeEach
    void setUp() {
        External external = External.builder()
                .title("test")
                .name("test")
                .external_code("test")
                .build();
        externalRepository.save(external);

        Profile profile1 = Profile.builder()
                .name("박승범")
                .profileImageUrl("url")
                .build();

        Member member = Member.builder()
                        .name("박승범")
                        .profile(profile1)
                        .build();

        Profile profile2 = Profile.builder()
                .name("사용자")
                .profileImageUrl("url")
                .build();

        Member member1 = Member.builder()
                .name("사용자")
                .profile(profile2)
                .build();

        memberRepository.saveAll(List.of(member, member1));

        Assignment assignment = Assignment.builder()
                .assignee(member)
                .external(external)
                .build();

        assigmentRepository.save(assignment);

    }

    @DisplayName("")
    @Test
    @Transactional
    void test(){

        //given
        ExternalRequestDTO.ExternalUpdateRequestDTO request = ExternalRequestDTO.ExternalUpdateRequestDTO.builder()
                .title("modified")
                .content("description")
                .managersId(List.of(1L, 2L))
                .build();

        //when
        externalService.updateExternal(1L, request);

        External external = externalRepository.findById(1L).get();

        external.getAssignments().stream()
                        .forEach(assignment -> {assignment.getAssignee().getId();});

        //then
        Assertions.assertThat(external).extracting("title", "content")
                .containsExactly("modified", "description");


        Assertions.assertThat(external.getAssignments())
                .extracting(assignment -> assignment.getAssignee().getId())
                .containsExactly(1L, 2L);
    }
}