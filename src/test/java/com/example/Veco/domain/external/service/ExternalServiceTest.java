package com.example.Veco.domain.external.service;

import com.example.Veco.domain.external.dto.request.ExternalRequestDTO;
import com.example.Veco.domain.external.dto.response.ExternalResponseDTO;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.external.repository.ExternalRepository;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.mapping.Assignment;
import com.example.Veco.domain.mapping.GithubInstallation;
import com.example.Veco.domain.mapping.entity.Link;
import com.example.Veco.domain.mapping.repository.AssigmentRepository;
import com.example.Veco.domain.mapping.repository.GitHubInstallationRepository;
import com.example.Veco.domain.mapping.repository.LinkRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.profile.entity.Profile;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.repository.TeamRepository;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import com.example.Veco.domain.workspace.repository.WorkspaceRepository;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
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

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private GitHubInstallationRepository gitHubInstallationRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private GoalRepository goalRepository;


    @BeforeEach
    void setUp() {
        External external = External.builder()
                .title("test")
                .name("test")
                .build();
        externalRepository.save(external);

        Profile profile1 = Profile.builder()
                .name("박승범")
                .profileImageUrl("url")
                .build();

        Profile profile2 = Profile.builder()
                .name("사용자")
                .profileImageUrl("url")
                .build();

        Member member = Member.builder()
                        .name("박승범")
                        .profile(profile1)
                        .email("email")
                        .build();

        Member member1 = Member.builder()
                .name("사용자")
                .profile(profile2)
                .email("email")
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

    @DisplayName("팀의 외부 서비스 연동 상태를 조회한다")
    @Test
    @Transactional
    void getExternalServices_should_return_link_status() {
        // given
        WorkSpace workSpace = WorkSpace.builder()
                .name("test-workspace")
                .build();
        workspaceRepository.save(workSpace);

        Team team = Team.builder()
                .name("test-team")
                .workSpace(workSpace)
                .build();
        teamRepository.save(team);

        Link slackLink = Link.builder()
                .workspace(workSpace)
                .build();
        linkRepository.save(slackLink);

        GithubInstallation githubInstallation = GithubInstallation.builder()
                .team(team)
                .installationId(12345L)
                .build();
        gitHubInstallationRepository.save(githubInstallation);

        // when
        ExternalResponseDTO.LinkInfoResponseDTO result = externalService.getExternalServices(team.getId());

        // then
        Assertions.assertThat(result.getLinkedWithSlack()).isTrue();
        Assertions.assertThat(result.getLinkedWithGithub()).isTrue();
    }

    @DisplayName("연동되지 않은 서비스들은 false를 반환한다")
    @Test
    @Rollback(false)
    void getExternalServices_should_return_false_for_unlinked_services() {
        // given
        WorkSpace workSpace = WorkSpace.builder()
                .name("test-workspace")
                .build();
        workspaceRepository.save(workSpace);

        Team team = Team.builder()
                .name("test-team")
                .workSpace(workSpace)
                .build();
        teamRepository.save(team);

        Goal goal = Goal.builder()
                .priority(Priority.HIGH)
                .state(State.NONE)
                .name("name")
                .title("title")
                .build();
        goalRepository.save(goal);

        // when
        ExternalResponseDTO.LinkInfoResponseDTO result = externalService.getExternalServices(team.getId());

        // then
        Assertions.assertThat(result.getLinkedWithSlack()).isFalse();
        Assertions.assertThat(result.getLinkedWithGithub()).isFalse();
    }


    @DisplayName("")
    @Test
    @Rollback(false)
    void test1(){

        //given
        Goal goal = Goal.builder()
                .priority(Priority.HIGH)
                .state(State.NONE)
                .name("name")
                .title("title")
                .build();
        goalRepository.save(goal);

        //when

        //then
    }
}