package com.example.Veco.domain.comment.service;

import com.example.Veco.domain.comment.dto.request.CommentCreateDTO;
import com.example.Veco.domain.comment.dto.response.CommentListResponseDTO;
import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.comment.entity.CommentRoom;
import com.example.Veco.domain.comment.repository.CommentRepository;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.external.exception.ExternalException;
import com.example.Veco.domain.external.repository.ExternalRepository;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.exception.GoalException;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.issue.exception.IssueException;
import com.example.Veco.domain.issue.repository.IssueRepository;
import com.example.Veco.domain.mapping.repository.CommentRoomRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.profile.entity.Profile;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.repository.TeamRepository;
import com.example.Veco.global.auth.user.userdetails.CustomUserDetails;
import com.example.Veco.global.enums.Category;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentRoomRepository commentRoomRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private IssueRepository issueRepository;
    
    @Autowired
    private GoalRepository goalRepository;
    
    @Autowired
    private ExternalRepository externalRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    private Member testMember;
    private Team testTeam;
    private Issue testIssue;
    private Goal testGoal;
    private External testExternal;

    @BeforeEach
    void setUp() {
        // 테스트용 팀 생성
        testTeam = Team.builder()
                .name("Test Team")
                .build();
        teamRepository.save(testTeam);
        
        // 테스트용 프로필 생성
        Profile testProfile = Profile.builder()
                .name("testUser")
                .profileImageUrl("http://example.com/profile.jpg")
                .build();
        entityManager.persist(testProfile);
        
        // 테스트용 멤버 생성
        testMember = Member.builder()
                .socialUid("test-uid")
                .email("test@example.com")
                .name("testUser")
                .nickname("testUser")
                .profile(testProfile)
                .build();
        memberRepository.save(testMember);
        
        // 테스트용 이슈 생성
        testIssue = Issue.builder()
                .title("Test Issue")
                .content("Test Issue Content")
                .name("Test Issue Name")
                .issue_number(1)
                .build();
        issueRepository.save(testIssue);
        
        // 테스트용 목표 생성
        testGoal = Goal.builder()
                .title("Test Goal")
                .content("Test Goal Content")
                .name("Test Goal")
                .build();
        goalRepository.save(testGoal);
        
        // 테스트용 외부 이슈 생성
        testExternal = External.builder()
                .title("Test External")
                .content("Test External Content")
                .external_number(2)
                .externalCode("TEST-EXT-001")
                .name("Test External Name")
                .build();
        externalRepository.save(testExternal);
        
        // Security Context 설정
        setupSecurityContext();
    }
    
    private void setupSecurityContext() {
        CustomUserDetails userDetails = new CustomUserDetails(testMember);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, null);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }


    @DisplayName("댓글 추가 성공 - ISSUE 카테고리")
    @Test
    void addComment_Success_Issue() {
        // given
        CommentCreateDTO commentCreateDTO = new CommentCreateDTO();
        commentCreateDTO.setContent("Test comment content");
        commentCreateDTO.setCategory(Category.ISSUE);
        commentCreateDTO.setTargetId(testIssue.getId());
        
        // when
        Long commentId = commentService.addComment(commentCreateDTO);
        
        // then
        assertThat(commentId).isNotNull();
        
        Comment savedComment = commentRepository.findById(commentId).orElse(null);
        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getContent()).isEqualTo("Test comment content");
        assertThat(savedComment.getMember().getId()).isEqualTo(testMember.getId());
    }
    
    @DisplayName("댓글 추가 성공 - GOAL 카테고리")
    @Test
    void addComment_Success_Goal() {
        // given
        CommentCreateDTO commentCreateDTO = new CommentCreateDTO();
        commentCreateDTO.setContent("Goal comment");
        commentCreateDTO.setCategory(Category.GOAL);
        commentCreateDTO.setTargetId(testGoal.getId());
        
        // when
        Long commentId = commentService.addComment(commentCreateDTO);
        
        // then
        assertThat(commentId).isNotNull();
        
        Comment savedComment = commentRepository.findById(commentId).orElse(null);
        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getContent()).isEqualTo("Goal comment");
    }
    
    @DisplayName("댓글 추가 성공 - EXTERNAL 카테고리")
    @Test
    void addComment_Success_External() {
        // given
        CommentCreateDTO commentCreateDTO = new CommentCreateDTO();
        commentCreateDTO.setContent("External comment");
        commentCreateDTO.setCategory(Category.EXTERNAL);
        commentCreateDTO.setTargetId(testExternal.getId());
        
        // when
        Long commentId = commentService.addComment(commentCreateDTO);
        
        // then
        assertThat(commentId).isNotNull();
        
        Comment savedComment = commentRepository.findById(commentId).orElse(null);
        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getContent()).isEqualTo("External comment");
    }
    
    @DisplayName("댓글 조회 성공 - 댓글이 있는 경우")
    @Test
    void getComments_Success_WithComments() {
        // given
        CommentCreateDTO commentCreateDTO1 = new CommentCreateDTO();
        commentCreateDTO1.setContent("First comment");
        commentCreateDTO1.setCategory(Category.ISSUE);
        commentCreateDTO1.setTargetId(testIssue.getId());
        
        CommentCreateDTO commentCreateDTO2 = new CommentCreateDTO();
        commentCreateDTO2.setContent("Second comment");
        commentCreateDTO2.setCategory(Category.ISSUE);
        commentCreateDTO2.setTargetId(testIssue.getId());
        
        commentService.addComment(commentCreateDTO1);
        commentService.addComment(commentCreateDTO2);
        
        // when
        CommentListResponseDTO response = commentService.getComments(testIssue.getId(), Category.ISSUE);
        
        // then
        // 총 댓글 수 검증
        assertThat(response.getTotalSize()).isEqualTo(2);
        assertThat(response.getComments()).hasSize(2);
        
        // 댓글 내용 검증 (최신순 정렬)
        assertThat(response.getComments().get(0).getContent()).isEqualTo("Second comment");
        assertThat(response.getComments().get(1).getContent()).isEqualTo("First comment");
        
        // 작성자 정보 검증
        CommentListResponseDTO.CommentResponseDTO firstComment = response.getComments().get(0);
        CommentListResponseDTO.CommentResponseDTO secondComment = response.getComments().get(1);
        
        assertThat(firstComment.getCommentId()).isNotNull();
        assertThat(firstComment.getAuthor().getAuthorId()).isEqualTo(testMember.getId());
        assertThat(firstComment.getAuthor().getAuthorName()).isEqualTo("testUser");
        assertThat(firstComment.getCreatedAt()).isNotNull();
        
        assertThat(secondComment.getCommentId()).isNotNull();
        assertThat(secondComment.getAuthor().getAuthorId()).isEqualTo(testMember.getId());
        assertThat(secondComment.getAuthor().getAuthorName()).isEqualTo("testUser");
        assertThat(secondComment.getCreatedAt()).isNotNull();
    }
    
    @DisplayName("댓글 조회 성공 - 댓글이 없는 경우")
    @Test
    void getComments_Success_NoComments() {
        // when
        CommentListResponseDTO response = commentService.getComments(testIssue.getId(), Category.ISSUE);
        
        // then
        assertThat(response.getTotalSize()).isEqualTo(0);
        assertThat(response.getComments()).isEmpty();
    }
    
    @DisplayName("댓글 조회 실패 - 존재하지 않는 리소스")
    @Test
    void getComments_Fail_ResourceNotFound() {
        // when & then
        assertThatThrownBy(() -> commentService.getComments(999L, Category.ISSUE))
                .isInstanceOf(IssueException.class);

        assertThatThrownBy(() -> commentService.getComments(999L, Category.GOAL))
                .isInstanceOf(GoalException.class);

        assertThatThrownBy(() -> commentService.getComments(999L, Category.EXTERNAL))
                .isInstanceOf(ExternalException.class);
    }
    
    @DisplayName("댓글방 생성 및 재사용 테스트")
    @Test
    void commentRoom_CreateAndReuse() {
        // given
        CommentCreateDTO commentCreateDTO1 = new CommentCreateDTO();
        commentCreateDTO1.setContent("First comment");
        commentCreateDTO1.setCategory(Category.ISSUE);
        commentCreateDTO1.setTargetId(testIssue.getId());
        
        CommentCreateDTO commentCreateDTO2 = new CommentCreateDTO();
        commentCreateDTO2.setContent("Second comment");
        commentCreateDTO2.setCategory(Category.ISSUE);
        commentCreateDTO2.setTargetId(testIssue.getId());
        
        // when
        commentService.addComment(commentCreateDTO1);
        commentService.addComment(commentCreateDTO2);
        
        // then
        List<CommentRoom> commentRooms = commentRoomRepository.findAll();
        long issueCommentRooms = commentRooms.stream()
                .filter(room -> room.getRoomType() == Category.ISSUE && room.getTargetId().equals(testIssue.getId()))
                .count();
        
        assertThat(issueCommentRooms).isEqualTo(1); // 댓글방이 재사용되어 하나만 생성되어야 함
    }
}