package com.example.Veco.test;

import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.external.repository.ExternalRepository;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.mapping.Assignment;
import com.example.Veco.domain.mapping.repository.AssigmentRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.enums.MemberRole;
import com.example.Veco.domain.member.enums.Provider;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.repository.TeamRepository;
import com.example.Veco.global.enums.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestDataController {

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private GoalRepository goalRepository;
    
    @Autowired
    private ExternalRepository externalRepository;
    
    @Autowired
    private AssigmentRepository assignmentRepository;

    @PostMapping("/insert-test-data")
    public String insertTestData() {
        try {
            // 1. Member 테스트 데이터
            List<Member> members = new ArrayList<>();
            members.add(Member.builder()
                    .name("김철수")
                    .nickname("chulsoo")
                    .email("chulsoo@test.com")
                    .provider(Provider.GOOGLE)
                    .role(MemberRole.USER)
                    .build());
            members.add(Member.builder()
                    .name("이영희")
                    .nickname("younghee")
                    .email("younghee@test.com")
                    .provider(Provider.GOOGLE)
                    .role(MemberRole.USER)
                    .build());
            members.add(Member.builder()
                    .name("박민수")
                    .nickname("minsu")
                    .email("minsu@test.com")
                    .provider(Provider.GOOGLE)
                    .role(MemberRole.USER)
                    .build());
            members.add(Member.builder()
                    .name("최지혜")
                    .nickname("jihye")
                    .email("jihye@test.com")
                    .provider(Provider.GOOGLE)
                    .role(MemberRole.USER)
                    .build());
            memberRepository.saveAll(members);

            // 2. Team 테스트 데이터
            Team team = Team.builder()
                    .name("테스트팀")
                    .build();
            teamRepository.save(team);

            // 3. Goal 테스트 데이터
            List<Goal> goals = new ArrayList<>();
            goals.add(Goal.builder()
                    .name("프론트엔드 개발")
                    .title("웹 프론트엔드 완성하기")
                    .content("리액트 기반 프론트엔드 개발")
                    .state(State.IN_PROGRESS)
                    .priority(Priority.HIGH)
                    .deadlineStart(LocalDate.of(2024, 1, 1))
                    .deadlineEnd(LocalDate.of(2024, 3, 31))
                    .team(team)
                    .build());
            goals.add(Goal.builder()
                    .name("백엔드 API")
                    .title("REST API 개발")
                    .content("스프링부트 백엔드 API 구현")
                    .state(State.TODO)
                    .priority(Priority.URGENT)
                    .deadlineStart(LocalDate.of(2024, 2, 1))
                    .deadlineEnd(LocalDate.of(2024, 4, 30))
                    .team(team)
                    .build());
            goals.add(Goal.builder()
                    .name("데이터베이스 설계")
                    .title("DB 스키마 설계")
                    .content("데이터베이스 스키마 및 관계 설정")
                    .state(State.FINISH)
                    .priority(Priority.NORMAL)
                    .deadlineStart(LocalDate.of(2023, 12, 1))
                    .deadlineEnd(LocalDate.of(2024, 1, 31))
                    .team(team)
                    .build());
            goals.add(Goal.builder()
                    .name("DevOps 구축")
                    .title("CI/CD 파이프라인")
                    .content("자동화 배포 시스템 구축")
                    .state(State.NONE)
                    .priority(Priority.LOW)
                    .team(team)
                    .build());
            goalRepository.saveAll(goals);

            // 4. External 테스트 데이터
            List<External> externals = new ArrayList<>();
            
            externals.add(External.builder()
                    .githubDataId(1001L)
                    .name("EXT-001")
                    .title("로그인 페이지 개발")
                    .description("OAuth 로그인 페이지 구현")
                    .state(State.NONE)
                    .member(members.get(0))
                    .priority(Priority.URGENT)
                    .startDate(LocalDate.of(2024, 1, 15))
                    .endDate(LocalDate.of(2024, 1, 30))
                    .type(ExtServiceType.GITHUB)
                    .team(team)
                    .goal(goals.get(0))
                    .build());

            externals.add(External.builder()
                    .githubDataId(1002L)
                    .name("EXT-002")
                    .title("회원가입 API")
                    .description("사용자 등록 REST API 개발")
                    .state(State.TODO)
                    .member(members.get(0))
                    .priority(Priority.HIGH)
                    .startDate(LocalDate.of(2024, 1, 20))
                    .endDate(LocalDate.of(2024, 2, 10))
                    .type(ExtServiceType.GITHUB)
                    .team(team)
                    .goal(goals.get(1))
                    .build());

            externals.add(External.builder()
                    .githubDataId(1003L)
                    .name("EXT-003")
                    .title("데이터베이스 스키마")
                    .description("User 테이블 스키마 설계")
                    .state(State.IN_PROGRESS)
                    .member(members.get(1))
                    .priority(Priority.HIGH)
                    .startDate(LocalDate.of(2024, 1, 10))
                    .endDate(LocalDate.of(2024, 1, 25))
                    .type(ExtServiceType.SLACK)
                    .team(team)
                    .goal(goals.get(2))
                    .build());

            externals.add(External.builder()
                    .githubDataId(1004L)
                    .name("EXT-004")
                    .title("비밀번호 암호화")
                    .description("BCrypt 암호화 적용")
                    .state(State.FINISH)
                    .member(members.get(1))
                    .priority(Priority.NORMAL)
                    .startDate(LocalDate.of(2024, 1, 5))
                    .endDate(LocalDate.of(2024, 1, 20))
                    .type(ExtServiceType.GITHUB)
                    .team(team)
                    .goal(goals.get(1))
                    .build());

            externals.add(External.builder()
                    .githubDataId(1005L)
                    .name("EXT-005")
                    .title("JWT 토큰 관리")
                    .description("JWT 인증 시스템 구현")
                    .state(State.REVIEW)
                    .member(members.get(2))
                    .priority(Priority.URGENT)
                    .startDate(LocalDate.of(2024, 1, 25))
                    .endDate(LocalDate.of(2024, 2, 15))
                    .type(ExtServiceType.NOTION)
                    .team(team)
                    .goal(goals.get(1))
                    .build());

            externals.add(External.builder()
                    .githubDataId(1006L)
                    .name("EXT-006")
                    .title("메인 대시보드 UI")
                    .description("메인 화면 컴포넌트 개발")
                    .state(State.TODO)
                    .member(members.get(0))
                    .priority(Priority.NONE)
                    .type(ExtServiceType.GITHUB)
                    .team(team)
                    .goal(goals.get(0))
                    .build());

            externals.add(External.builder()
                    .githubDataId(1007L)
                    .name("EXT-007")
                    .title("보안 취약점 점검")
                    .description("OWASP 보안 점검")
                    .state(State.IN_PROGRESS)
                    .member(members.get(2))
                    .priority(Priority.URGENT)
                    .startDate(LocalDate.of(2024, 2, 1))
                    .endDate(LocalDate.of(2024, 2, 10))
                    .type(ExtServiceType.SLACK)
                    .team(team)
                    .build());

            externals.add(External.builder()
                    .githubDataId(1008L)
                    .name("EXT-008")
                    .title("성능 최적화")
                    .description("DB 쿼리 최적화")
                    .state(State.NONE)
                    .member(members.get(3))
                    .priority(Priority.LOW)
                    .type(ExtServiceType.SLACK)
                    .team(team)
                    .goal(goals.get(2))
                    .build());

            externals.add(External.builder()
                    .githubDataId(1009L)
                    .name("EXT-009")
                    .title("단위 테스트 작성")
                    .description("Service 레이어 테스트")
                    .state(State.TODO)
                    .member(members.get(1))
                    .priority(Priority.NORMAL)
                    .startDate(LocalDate.of(2024, 2, 5))
                    .endDate(LocalDate.of(2024, 2, 20))
                    .type(ExtServiceType.GITHUB)
                    .team(team)
                    .goal(goals.get(1))
                    .build());

            externals.add(External.builder()
                    .githubDataId(1010L)
                    .name("EXT-010")
                    .title("문서화 작업")
                    .description("API 문서 Swagger 적용")
                    .state(State.REVIEW)
                    .member(members.get(3))
                    .priority(Priority.HIGH)
                    .startDate(LocalDate.of(2024, 1, 30))
                    .endDate(LocalDate.of(2024, 2, 15))
                    .type(ExtServiceType.NOTION)
                    .team(team)
                    .build());

            externals.add(External.builder()
                    .githubDataId(1015L)
                    .name("EXT-015")
                    .title("임시 버그 수정")
                    .description("긴급 버그 핫픽스")
                    .state(State.TODO) // 'URGENT' 상태는 존재하지 않으므로 TODO로 변경
                    .member(members.get(0))
                    .priority(Priority.URGENT)
                    .startDate(LocalDate.of(2024, 2, 8))
                    .endDate(LocalDate.of(2024, 2, 8))
                    .type(ExtServiceType.GITHUB)
                    .team(team)
                    .build());

            externals.add(External.builder()
                    .githubDataId(1016L)
                    .name("EXT-016")
                    .title("서버 모니터링")
                    .description("서버 상태 모니터링 설정")
                    .state(State.IN_PROGRESS)
                    .member(members.get(2))
                    .priority(Priority.HIGH)
                    .startDate(LocalDate.of(2024, 2, 1))
                    .endDate(LocalDate.of(2024, 2, 29))
                    .type(ExtServiceType.SLACK)
                    .team(team)
                    .build());

            externalRepository.saveAll(externals);

            // 5. Assignment 테스트 데이터
            List<Assignment> assignments = new ArrayList<>();
            
            // EXT-001: 김철수
            assignments.add(Assignment.builder()
                    .category(Category.EXTERNAL)
                    .assigneeName("김철수")
                    .assignee(members.get(0))
                    .external(externals.get(0))
                    .build());

            // EXT-002: 김철수 + 이영희 (복수 담당자)
            assignments.add(Assignment.builder()
                    .category(Category.EXTERNAL)
                    .assigneeName("김철수")
                    .assignee(members.get(0))
                    .external(externals.get(1))
                    .build());
            assignments.add(Assignment.builder()
                    .category(Category.EXTERNAL)
                    .assigneeName("이영희")
                    .assignee(members.get(1))
                    .external(externals.get(1))
                    .build());

            // EXT-003: 이영희
            assignments.add(Assignment.builder()
                    .category(Category.EXTERNAL)
                    .assigneeName("이영희")
                    .assignee(members.get(1))
                    .external(externals.get(2))
                    .build());

            // EXT-004: 이영희 + 박민수 (복수 담당자)
            assignments.add(Assignment.builder()
                    .category(Category.EXTERNAL)
                    .assigneeName("이영희")
                    .assignee(members.get(1))
                    .external(externals.get(3))
                    .build());
            assignments.add(Assignment.builder()
                    .category(Category.EXTERNAL)
                    .assigneeName("박민수")
                    .assignee(members.get(2))
                    .external(externals.get(3))
                    .build());

            // EXT-005: 박민수
            assignments.add(Assignment.builder()
                    .category(Category.EXTERNAL)
                    .assigneeName("박민수")
                    .assignee(members.get(2))
                    .external(externals.get(4))
                    .build());

            // EXT-006: 담당자 없음
            
            // EXT-007: 박민수 + 최지혜 (복수 담당자)
            assignments.add(Assignment.builder()
                    .category(Category.EXTERNAL)
                    .assigneeName("박민수")
                    .assignee(members.get(2))
                    .external(externals.get(6))
                    .build());
            assignments.add(Assignment.builder()
                    .category(Category.EXTERNAL)
                    .assigneeName("최지혜")
                    .assignee(members.get(3))
                    .external(externals.get(6))
                    .build());

            // EXT-008: 최지혜
            assignments.add(Assignment.builder()
                    .category(Category.EXTERNAL)
                    .assigneeName("최지혜")
                    .assignee(members.get(3))
                    .external(externals.get(7))
                    .build());

            // EXT-015: 김철수 + 이영희 + 박민수 (3명 담당자)
            assignments.add(Assignment.builder()
                    .category(Category.EXTERNAL)
                    .assigneeName("김철수")
                    .assignee(members.get(0))
                    .external(externals.get(10))
                    .build());
            assignments.add(Assignment.builder()
                    .category(Category.EXTERNAL)
                    .assigneeName("이영희")
                    .assignee(members.get(1))
                    .external(externals.get(10))
                    .build());
            assignments.add(Assignment.builder()
                    .category(Category.EXTERNAL)
                    .assigneeName("박민수")
                    .assignee(members.get(2))
                    .external(externals.get(10))
                    .build());

            // EXT-016: 박민수
            assignments.add(Assignment.builder()
                    .category(Category.EXTERNAL)
                    .assigneeName("박민수")
                    .assignee(members.get(2))
                    .external(externals.get(11))
                    .build());

            assignmentRepository.saveAll(assignments);

            return "테스트 데이터가 성공적으로 삽입되었습니다!\\n" +
                   "- Members: " + members.size() + "\\n" +
                   "- Team ID: " + team.getId() + "\\n" +
                   "- Goals: " + goals.size() + "\\n" +
                   "- Externals: " + externals.size() + "\\n" +
                   "- Assignments: " + assignments.size();

        } catch (Exception e) {
            return "테스트 데이터 삽입 중 오류 발생: " + e.getMessage();
        }
    }
}