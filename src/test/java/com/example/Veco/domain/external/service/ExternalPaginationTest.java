package com.example.Veco.domain.external.service;

import com.example.Veco.domain.external.dto.ExternalResponseDTO;
import com.example.Veco.domain.external.dto.ExternalSearchCriteria;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.external.repository.ExternalRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.repository.TeamRepository;
import com.example.Veco.global.apiPayload.page.CursorPage;
import com.example.Veco.global.enums.ExtServiceType;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ExternalPaginationTest {

    @Autowired
    private ExternalService externalService;
    
    @Autowired
    private ExternalRepository externalRepository;
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private MemberRepository memberRepository;

    private Team testTeam;
    private Member testMember;

    @BeforeEach
    void setUp() {
        testTeam = Team.builder()
                .name("TestTeam")
                .build();
        teamRepository.save(testTeam);

        testMember = Member.builder()
                .name("TestMember")
                .build();
        memberRepository.save(testMember);
    }

    @DisplayName("페이지네이션 서비스 레이어 테스트: 상태별 우선순위 정렬")
    @Test
    void testGetExternalsWithPaginationStatePriority() {
        // given
        createExternalWithState("NONE_External", State.NONE);
        createExternalWithState("TODO_External", State.TODO);
        createExternalWithState("IN_PROGRESS_External", State.IN_PROGRESS);
        createExternalWithState("DONE_External", State.DONE);
        createExternalWithState("REVIEW_External", State.REVIEW);

        ExternalSearchCriteria criteria = ExternalSearchCriteria.builder().build();

        // when
        CursorPage<ExternalResponseDTO.ExternalDTO> result = 
            externalService.getExternalsWithPagination(criteria, null, 10);

        // then
        List<ExternalResponseDTO.ExternalDTO> externals = result.getData();
        assertThat(externals).hasSize(5);
        
        // 상태 순서 확인: NONE -> TODO -> IN_PROGRESS -> DONE -> REVIEW
        assertThat(externals.get(0).getState()).isEqualTo(State.NONE);
        assertThat(externals.get(1).getState()).isEqualTo(State.TODO);
        assertThat(externals.get(2).getState()).isEqualTo(State.IN_PROGRESS);
        assertThat(externals.get(3).getState()).isEqualTo(State.DONE);
        assertThat(externals.get(4).getState()).isEqualTo(State.REVIEW);
    }

    @DisplayName("페이지네이션 서비스 레이어 테스트: 50개 제한")
    @Test
    void testGetExternalsWithPaginationLimit() {
        // given - 60개의 NONE 상태 External 생성
        for (int i = 0; i < 60; i++) {
            createExternalWithState("NONE_External_" + i, State.NONE);
        }

        ExternalSearchCriteria criteria = ExternalSearchCriteria.builder().build();

        // when
        CursorPage<ExternalResponseDTO.ExternalDTO> result = 
            externalService.getExternalsWithPagination(criteria, null, 50);

        // then
        assertThat(result.getData()).hasSize(50);
        assertThat(result.isHasNext()).isTrue();
        assertThat(result.getNextCursor()).isNotNull();
    }

    @DisplayName("페이지네이션 서비스 레이어 테스트: 상태 필터링")
    @Test
    void testGetExternalsWithPaginationStateFilter() {
        // given
        createExternalWithState("NONE_External", State.NONE);
        createExternalWithState("TODO_External_1", State.TODO);
        createExternalWithState("TODO_External_2", State.TODO);
        createExternalWithState("IN_PROGRESS_External", State.IN_PROGRESS);

        ExternalSearchCriteria criteria = ExternalSearchCriteria.builder()
                .state(State.TODO)
                .build();

        // when
        CursorPage<ExternalResponseDTO.ExternalDTO> result = 
            externalService.getExternalsWithPagination(criteria, null, 10);

        // then
        List<ExternalResponseDTO.ExternalDTO> externals = result.getData();
        assertThat(externals).hasSize(2);
        assertThat(externals).allMatch(external -> external.getState() == State.TODO);
    }

    @DisplayName("페이지네이션 서비스 레이어 테스트: 우선순위 필터링")
    @Test
    void testGetExternalsWithPaginationPriorityFilter() {
        // given
        createExternalWithPriority("LOW_External", Priority.LOW);
        createExternalWithPriority("HIGH_External_1", Priority.HIGH);
        createExternalWithPriority("HIGH_External_2", Priority.HIGH);
        createExternalWithPriority("MEDIUM_External", Priority.NORMAL);

        ExternalSearchCriteria criteria = ExternalSearchCriteria.builder()
                .priority(Priority.HIGH)
                .build();

        // when
        CursorPage<ExternalResponseDTO.ExternalDTO> result = 
            externalService.getExternalsWithPagination(criteria, null, 10);

        // then
        List<ExternalResponseDTO.ExternalDTO> externals = result.getData();
        assertThat(externals).hasSize(2);
        assertThat(externals).allMatch(external -> external.getPriority() == Priority.HIGH);
    }

    @DisplayName("페이지네이션 서비스 레이어 테스트: 커서 기반 페이지네이션")
    @Test
    void testGetExternalsWithPaginationCursor() {
        // given - 10개의 NONE 상태 External 생성
        for (int i = 0; i < 10; i++) {
            createExternalWithState("NONE_External_" + i, State.NONE);
        }

        ExternalSearchCriteria criteria = ExternalSearchCriteria.builder().build();

        // when - 첫 번째 페이지 (5개)
        CursorPage<ExternalResponseDTO.ExternalDTO> firstPage = 
            externalService.getExternalsWithPagination(criteria, null, 5);

        // then - 첫 번째 페이지 검증
        assertThat(firstPage.getData()).hasSize(5);
        assertThat(firstPage.isHasNext()).isTrue();
        assertThat(firstPage.getNextCursor()).isNotNull();

        // when - 두 번째 페이지
        CursorPage<ExternalResponseDTO.ExternalDTO> secondPage = 
            externalService.getExternalsWithPagination(criteria, firstPage.getNextCursor(), 5);

        // then - 두 번째 페이지 검증
        assertThat(secondPage.getData()).hasSize(5);
        assertThat(secondPage.isHasNext()).isFalse();
        
        // 두 페이지의 내용이 다른지 확인
        List<Long> firstPageIds = firstPage.getData().stream()
                .map(ExternalResponseDTO.ExternalDTO::getId)
                .toList();
        List<Long> secondPageIds = secondPage.getData().stream()
                .map(ExternalResponseDTO.ExternalDTO::getId)
                .toList();
        
        assertThat(firstPageIds).doesNotContainAnyElementsOf(secondPageIds);
    }

    private void createExternalWithState(String name, State state) {
        External external = External.builder()
                .name(name)
                .title(name + "_Title")
                .description(name + "_Description")
                .state(state)
                .member(testMember)
                .priority(Priority.NORMAL)
                .type(ExtServiceType.GITHUB)
                .external_code(name + "_CODE")
                .team(testTeam)
                .build();
        
        externalRepository.save(external);
    }

    private void createExternalWithPriority(String name, Priority priority) {
        External external = External.builder()
                .name(name)
                .title(name + "_Title")
                .description(name + "_Description")
                .state(State.NONE)
                .member(testMember)
                .priority(priority)
                .type(ExtServiceType.GITHUB)
                .external_code(name + "_CODE")
                .team(testTeam)
                .build();
        
        externalRepository.save(external);
    }
}