package com.example.Veco.domain.external.repository;

import com.example.Veco.domain.external.dto.response.ExternalResponseDTO;
import com.example.Veco.domain.external.dto.response.ExternalGroupedResponseDTO;
import com.example.Veco.domain.external.dto.paging.ExternalSearchCriteria;
import com.example.Veco.domain.external.dto.request.ExternalRequestDTO;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.repository.TeamRepository;
import com.example.Veco.global.apiPayload.page.CursorPage;
import com.example.Veco.global.enums.ExtServiceType;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ExternalCursorRepositoryTest {

    @Autowired
    private ExternalCursorRepository externalCursorRepository;
    
    @Autowired
    private ExternalRepository externalRepository;
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private EntityManager entityManager;

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
                .email("test@example.com")
                .nickname("testNick")
                .build();
        memberRepository.save(testMember);
    }

    @DisplayName("상태별 우선순위 정렬 테스트: NONE -> TODO -> IN_PROGRESS -> FINISH -> REVIEW")
    @Test
    void testStatePriorityOrder() {
        // given
        createExternalWithState("NONE_External", State.NONE);
        createExternalWithState("TODO_External", State.TODO);
        createExternalWithState("IN_PROGRESS_External", State.IN_PROGRESS);
        createExternalWithState("FINISH_External", State.FINISH);
        createExternalWithState("REVIEW_External", State.REVIEW);

        ExternalSearchCriteria criteria = ExternalSearchCriteria.builder()
                .teamId(testTeam.getId())
                .build();

        // when
        CursorPage<ExternalResponseDTO.ExternalDTO> result =
            externalCursorRepository.findExternalWithCursor(criteria, null, 10);

        // then
        List<ExternalResponseDTO.ExternalDTO> externals = result.getData();
        assertThat(externals).hasSize(5);
        
        // 상태 순서 확인: NONE -> TODO -> IN_PROGRESS -> FINISH -> REVIEW
        assertThat(externals.get(0).getState()).isEqualTo(State.NONE);
        assertThat(externals.get(1).getState()).isEqualTo(State.TODO);
        assertThat(externals.get(2).getState()).isEqualTo(State.IN_PROGRESS);
        assertThat(externals.get(3).getState()).isEqualTo(State.FINISH);
        assertThat(externals.get(4).getState()).isEqualTo(State.REVIEW);
    }

    @DisplayName("50개 제한 테스트: 정확히 50개만 반환")
    @Test
    void testFiftyItemsLimit() {
        // given - 60개의 NONE 상태 External 생성
        for (int i = 0; i < 60; i++) {
            createExternalWithState("NONE_External_" + i, State.NONE);
        }

        ExternalSearchCriteria criteria = ExternalSearchCriteria.builder()
                .teamId(testTeam.getId())
                .build();

        // when
        CursorPage<ExternalResponseDTO.ExternalDTO> result =
            externalCursorRepository.findExternalWithCursor(criteria, null, 50);

        // then
        assertThat(result.getData()).hasSize(50);
        assertThat(result.isHasNext()).isTrue();
    }

    @DisplayName("상태 필터링 테스트: TODO 상태만 조회")
    @Test
    void testStateFiltering() {
        // given
        createExternalWithState("NONE_External", State.NONE);
        createExternalWithState("TODO_External_1", State.TODO);
        createExternalWithState("TODO_External_2", State.TODO);
        createExternalWithState("IN_PROGRESS_External", State.IN_PROGRESS);

        ExternalSearchCriteria criteria = ExternalSearchCriteria.builder()
                .teamId(testTeam.getId())
                .state(State.TODO)
                .build();

        // when
        CursorPage<ExternalResponseDTO.ExternalDTO> result =
            externalCursorRepository.findExternalWithCursor(criteria, null, 10);

        // then
        List<ExternalResponseDTO.ExternalDTO> externals = result.getData();
        assertThat(externals).hasSize(2);
        assertThat(externals).allMatch(external -> external.getState() == State.TODO);
    }

    @DisplayName("우선순위 필터링 테스트: HIGH 우선순위만 조회")
    @Test
    void testPriorityFiltering() {
        // given
        createExternalWithPriority("LOW_External", Priority.LOW);
        createExternalWithPriority("HIGH_External_1", Priority.HIGH);
        createExternalWithPriority("HIGH_External_2", Priority.HIGH);
        createExternalWithPriority("NORNAL_External", Priority.NORMAL);

        ExternalSearchCriteria criteria = ExternalSearchCriteria.builder()
                .teamId(testTeam.getId())
                .priority(Priority.HIGH)
                .build();

        // when
        CursorPage<ExternalResponseDTO.ExternalDTO> result =
            externalCursorRepository.findExternalWithCursor(criteria, null, 10);

        // then
        List<ExternalResponseDTO.ExternalDTO> externals = result.getData();
        assertThat(externals).hasSize(2);
        assertThat(externals).allMatch(external -> external.getPriority() == Priority.HIGH);
    }

    @DisplayName("커서 기반 페이지네이션 테스트")
    @Test
    void testCursorPagination() {
        // given - 10개의 NONE 상태 External 생성
        for (int i = 0; i < 10; i++) {
            createExternalWithState("NONE_External_" + i, State.NONE);
        }

        ExternalSearchCriteria criteria = ExternalSearchCriteria.builder()
                .teamId(testTeam.getId())
                .build();

        // when - 첫 번째 페이지 (5개)
        CursorPage<ExternalResponseDTO.ExternalDTO> firstPage =
            externalCursorRepository.findExternalWithCursor(criteria, null, 5);

        // then - 첫 번째 페이지 검증
        assertThat(firstPage.getData()).hasSize(5);
        assertThat(firstPage.isHasNext()).isTrue();
        assertThat(firstPage.getNextCursor()).isNotNull();

        // when - 두 번째 페이지
        CursorPage<ExternalResponseDTO.ExternalDTO> secondPage =
            externalCursorRepository.findExternalWithCursor(criteria, firstPage.getNextCursor(), 5);

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

    @DisplayName("빈 결과 테스트")
    @Test
    void testEmptyResult() {
        // given - 데이터 없음
        ExternalSearchCriteria criteria = ExternalSearchCriteria.builder()
                .teamId(testTeam.getId())
                .state(State.REVIEW)
                .build();

        // when
        CursorPage<ExternalResponseDTO.ExternalDTO> result =
            externalCursorRepository.findExternalWithCursor(criteria, null, 10);

        // then
        assertThat(result.getData()).isEmpty();
        assertThat(result.isHasNext()).isFalse();
        assertThat(result.getNextCursor()).isNull();
    }

    @DisplayName("상태별 그룹핑 테스트: 각 상태별로 그룹화되어 반환")
    @Test
    void testStateGrouping() {
        // given
        createExternalWithState("NONE_External_1", State.NONE);
        createExternalWithState("NONE_External_2", State.NONE);
        createExternalWithState("TODO_External_1", State.TODO);
        createExternalWithState("IN_PROGRESS_External_1", State.IN_PROGRESS);
        createExternalWithState("FINISH_External_1", State.FINISH);

        ExternalSearchCriteria criteria = ExternalSearchCriteria.builder()
                .teamId(testTeam.getId())
                .filterType(ExternalRequestDTO.ExternalGroupedSearchRequestDTO.FilterType.STATE)
                .build();

        // when
        ExternalGroupedResponseDTO.ExternalGroupedPageResponse result =
            externalCursorRepository.findExternalWithGroupedResponse(criteria, null, 50);

        // then
        assertThat(result.getData()).hasSize(5); // 모든 상태 그룹이 포함됨: NONE, IN_PROGRESS, TODO, FINISH, REVIEW
        
        // 그룹 순서 확인: NONE -> IN_PROGRESS -> TODO -> FINISH
        ExternalGroupedResponseDTO.FilteredExternalGroup noneGroup = result.getData().get(0);
        assertThat(noneGroup.getFilterName()).isEqualTo("NONE");
        assertThat(noneGroup.getDataCnt()).isEqualTo(2);
        
        ExternalGroupedResponseDTO.FilteredExternalGroup inProgressGroup = result.getData().get(1);
        assertThat(inProgressGroup.getFilterName()).isEqualTo("IN_PROGRESS");
        assertThat(inProgressGroup.getDataCnt()).isEqualTo(1);
        
        ExternalGroupedResponseDTO.FilteredExternalGroup todoGroup = result.getData().get(2);
        assertThat(todoGroup.getFilterName()).isEqualTo("TODO");
        assertThat(todoGroup.getDataCnt()).isEqualTo(1);
        
        ExternalGroupedResponseDTO.FilteredExternalGroup finishGroup = result.getData().get(3);
        assertThat(finishGroup.getFilterName()).isEqualTo("FINISH");
        assertThat(finishGroup.getDataCnt()).isEqualTo(1);
    }

    @DisplayName("우선순위별 그룹핑 테스트: 각 우선순위별로 그룹화되어 반환")
    @Test
    void testPriorityGrouping() {
        // given
        createExternalWithPriority("NONE_External_1", Priority.NONE);
        createExternalWithPriority("NONE_External_2", Priority.NONE);
        createExternalWithPriority("URGENT_External_1", Priority.URGENT);
        createExternalWithPriority("HIGH_External_1", Priority.HIGH);
        createExternalWithPriority("NORMAL_External_1", Priority.NORMAL);

        ExternalSearchCriteria criteria = ExternalSearchCriteria.builder()
                .teamId(testTeam.getId())
                .filterType(ExternalRequestDTO.ExternalGroupedSearchRequestDTO.FilterType.PRIORITY)
                .build();

        // when
        ExternalGroupedResponseDTO.ExternalGroupedPageResponse result =
            externalCursorRepository.findExternalWithGroupedResponse(criteria, null, 50);

        // then
        assertThat(result.getData()).hasSize(5); // 모든 우선순위 그룹이 포함됨: NONE, URGENT, HIGH, NORMAL, LOW
        
        // 그룹 순서 확인: NONE -> URGENT -> HIGH -> NORMAL
        ExternalGroupedResponseDTO.FilteredExternalGroup noneGroup = result.getData().get(0);
        assertThat(noneGroup.getFilterName()).isEqualTo("NONE");
        assertThat(noneGroup.getDataCnt()).isEqualTo(2);
        
        ExternalGroupedResponseDTO.FilteredExternalGroup urgentGroup = result.getData().get(1);
        assertThat(urgentGroup.getFilterName()).isEqualTo("URGENT");
        assertThat(urgentGroup.getDataCnt()).isEqualTo(1);
        
        ExternalGroupedResponseDTO.FilteredExternalGroup highGroup = result.getData().get(2);
        assertThat(highGroup.getFilterName()).isEqualTo("HIGH");
        assertThat(highGroup.getDataCnt()).isEqualTo(1);
        
        ExternalGroupedResponseDTO.FilteredExternalGroup normalGroup = result.getData().get(3);
        assertThat(normalGroup.getFilterName()).isEqualTo("NORMAL");
        assertThat(normalGroup.getDataCnt()).isEqualTo(1);
    }

    @DisplayName("그룹핑 50개 제한 테스트: 그룹 전체에서 50개만 반환")
    @Test
    @Rollback(false)
    void testGroupingFiftyItemsLimit() {
        // given - NONE 상태 45개, TODO 상태 30개 생성 (총 75개)
        for (int i = 0; i < 45; i++) {
            createExternalWithState("NONE_External_" + i, State.NONE);
        }
        for (int i = 0; i < 30; i++) {
            createExternalWithState("TODO_External_" + i, State.TODO);
        }

        ExternalSearchCriteria criteria = ExternalSearchCriteria.builder()
                .teamId(testTeam.getId())
                .filterType(ExternalRequestDTO.ExternalGroupedSearchRequestDTO.FilterType.STATE)
                .build();

        // when
        ExternalGroupedResponseDTO.ExternalGroupedPageResponse result =
            externalCursorRepository.findExternalWithGroupedResponse(criteria, null, 50);

        // then
        assertThat(result.isHasNext()).isTrue();
        assertThat(result.getNextCursor()).isNotNull();
        
        // NONE 그룹에서 45개, TODO 그룹에서 5개만 조회되어야 함
        int totalItems = result.getData().stream()
                .mapToInt(ExternalGroupedResponseDTO.FilteredExternalGroup::getDataCnt)
                .sum();
        assertThat(totalItems).isEqualTo(50);
        
        // NONE 그룹이 먼저 나와야 함
        ExternalGroupedResponseDTO.FilteredExternalGroup firstGroup = result.getData().get(0);
        assertThat(firstGroup.getFilterName()).isEqualTo("NONE");
        assertThat(firstGroup.getDataCnt()).isEqualTo(45);
        
        // IN_PROGRESS 그룹은 빈 그룹이어야 함
        if (result.getData().size() > 1) {
            ExternalGroupedResponseDTO.FilteredExternalGroup secondGroup = result.getData().get(1);
            assertThat(secondGroup.getFilterName()).isEqualTo("IN_PROGRESS");
            assertThat(secondGroup.getDataCnt()).isEqualTo(0);
        }
        
        // TODO 그룹에서 5개만 조회되어야 함
        if (result.getData().size() > 2) {
            ExternalGroupedResponseDTO.FilteredExternalGroup thirdGroup = result.getData().get(2);
            assertThat(thirdGroup.getFilterName()).isEqualTo("TODO");
            assertThat(thirdGroup.getDataCnt()).isEqualTo(5);
        }
    }

    @DisplayName("빈 그룹도 응답에 포함되는지 테스트")
    @Test
    void testEmptyGroupsIncluded() {
        // given - NONE 상태만 2개 생성 (다른 상태는 없음)
        createExternalWithState("NONE_External_1", State.NONE);
        createExternalWithState("NONE_External_2", State.NONE);

        ExternalSearchCriteria criteria = ExternalSearchCriteria.builder()
                .teamId(testTeam.getId())
                .filterType(ExternalRequestDTO.ExternalGroupedSearchRequestDTO.FilterType.STATE)
                .build();

        // when
        ExternalGroupedResponseDTO.ExternalGroupedPageResponse result =
            externalCursorRepository.findExternalWithGroupedResponse(criteria, null, 50);

        // then
        assertThat(result.getData()).hasSize(5); // 모든 상태 그룹이 포함되어야 함: NONE, IN_PROGRESS, TODO, FINISH, REVIEW
        
        // NONE 그룹은 데이터가 있어야 함
        ExternalGroupedResponseDTO.FilteredExternalGroup noneGroup = result.getData().get(0);
        assertThat(noneGroup.getFilterName()).isEqualTo("NONE");
        assertThat(noneGroup.getDataCnt()).isEqualTo(2);
        assertThat(noneGroup.getExternals()).hasSize(2);
        
        // IN_PROGRESS 그룹은 빈 그룹이어야 함
        ExternalGroupedResponseDTO.FilteredExternalGroup inProgressGroup = result.getData().get(1);
        assertThat(inProgressGroup.getFilterName()).isEqualTo("IN_PROGRESS");
        assertThat(inProgressGroup.getDataCnt()).isEqualTo(0);
        assertThat(inProgressGroup.getExternals()).isEmpty();
        
        // TODO 그룹은 빈 그룹이어야 함
        ExternalGroupedResponseDTO.FilteredExternalGroup todoGroup = result.getData().get(2);
        assertThat(todoGroup.getFilterName()).isEqualTo("TODO");
        assertThat(todoGroup.getDataCnt()).isEqualTo(0);
        assertThat(todoGroup.getExternals()).isEmpty();
        
        // FINISH 그룹은 빈 그룹이어야 함
        ExternalGroupedResponseDTO.FilteredExternalGroup finishGroup = result.getData().get(3);
        assertThat(finishGroup.getFilterName()).isEqualTo("FINISH");
        assertThat(finishGroup.getDataCnt()).isEqualTo(0);
        assertThat(finishGroup.getExternals()).isEmpty();
        
        // REVIEW 그룹은 빈 그룹이어야 함
        ExternalGroupedResponseDTO.FilteredExternalGroup reviewGroup = result.getData().get(4);
        assertThat(reviewGroup.getFilterName()).isEqualTo("REVIEW");
        assertThat(reviewGroup.getDataCnt()).isEqualTo(0);
        assertThat(reviewGroup.getExternals()).isEmpty();
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
                .team(testTeam)
                .build();
        
        externalRepository.saveAndFlush(external);
        entityManager.clear();
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
                .team(testTeam)
                .build();
        
        externalRepository.saveAndFlush(external);
        entityManager.clear();
    }
}