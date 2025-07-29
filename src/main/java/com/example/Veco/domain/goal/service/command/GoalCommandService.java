package com.example.Veco.domain.goal.service.command;

import com.example.Veco.domain.common.entity.Image;
import com.example.Veco.domain.common.repository.ImageRepository;
import com.example.Veco.domain.goal.converter.GoalConverter;
import com.example.Veco.domain.goal.dto.request.GoalReqDTO;
import com.example.Veco.domain.goal.dto.response.GoalResDTO;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.CreateGoal;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.exception.GoalException;
import com.example.Veco.domain.goal.exception.code.GoalErrorCode;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.issue.exception.IssueException;
import com.example.Veco.domain.issue.exception.code.IssueErrorCode;
import com.example.Veco.domain.issue.repository.IssueRepository;
import com.example.Veco.domain.mapping.entity.MemberTeam;
import com.example.Veco.domain.mapping.repository.MemberTeamRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.error.MemberErrorStatus;
import com.example.Veco.domain.member.error.MemberHandler;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.team.exception.TeamException;
import com.example.Veco.domain.team.exception.code.TeamErrorCode;
import com.example.Veco.domain.team.repository.TeamRepository;
import com.example.Veco.global.auth.user.AuthUser;
import com.example.Veco.global.aws.util.S3Util;
import com.example.Veco.global.redis.exception.RedisException;
import com.example.Veco.global.redis.exception.code.RedisErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalCommandService {

    // 유틸
    private final S3Util s3Util;

    // 리포지토리
    private final GoalRepository goalRepository;
    private final ImageRepository imageRepository;
    private final MemberRepository memberRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final IssueRepository issueRepository;
    private final TeamRepository teamRepository;
    private final RedissonClient redissonClient;

    // 트랜잭션 서비스
    private final GoalTransactionalService goalTransactionalService;

    // 목표 작성
    public CreateGoal createGoal(
            Long teamId,
            GoalReqDTO.CreateGoal dto,
            AuthUser user
    ) {
        // 담당자 존재 여부, 같은 팀 여부 검증 + 본인 포함 여부 확인 후 업데이트
        List<Long> memberIds = new ArrayList<>(dto.managersId());
        if (dto.isIncludeMe()) {
            // 인증 객체에서 가져오기
            Member member = memberRepository.findBySocialUid(user.getSocialUid()).orElseThrow(() ->
                    new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND));
            memberIds.add(member.getId());
        }

        // 사용자 존재 여부 검증
        List<Member> memberList = memberRepository.findAllById(memberIds);
        if (memberList.size() != memberIds.size()) {
            throw new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND);
        }

        // 팀 존재 여부 검증
        if (!teamRepository.existsById(teamId)) {

            throw new TeamException(TeamErrorCode._NOT_FOUND);
        }

        // 같은 팀원 여부 검증
        List<MemberTeam> memberTeamList = memberTeamRepository.findAllByMemberIdInAndTeamId(memberIds, teamId);
        if (memberTeamList.size() != memberIds.size()) {
            throw new MemberHandler(MemberErrorStatus._FORBIDDEN);
        }

        // 이슈 존재 여부 검증
        List<Issue> issueList = issueRepository.findAllById(dto.issueId());
        if (issueList.size() != dto.issueId().size()) {
            throw new IssueException(IssueErrorCode.NOT_FOUND);
        }

        // 목표 생성: DTO, Team, Name 필요, @Transactional
        // 목표 이름(Veco-g3) 생성 로직: 분산 락 걸고 이름 조회, +1
        RLock lock = redissonClient.getLock("lock:goal:" + teamId);
        Long goalId;
        try {
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);
            if (!available) {
                throw new RedisException(RedisErrorCode.LOCK_TIMEOUT);
            }
            // 파사드 기법으로 @Transactional 진행 후 락 해제
            goalId = goalTransactionalService.createGoal(teamId, dto, memberTeamList, issueList);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        LocalDateTime now = LocalDateTime.now();
        return GoalConverter.toCreateGoal(goalId, now);
    }

    // 목표 사진 첨부
    public String uploadFile(
            MultipartFile file
    ){
        // 사진 업로드
        String url = s3Util.getImageUrl(s3Util.uploadFile(file, null));

        // 링크 저장
        Image image = Image.builder().url(url).build();
        imageRepository.save(image);

        return url;
    }

    // 목표 수정
    public GoalResDTO.UpdateGoal updateGoal(
            GoalReqDTO.UpdateGoal dto,
            Long teamId,
            Long goalId
    ){
        // 사용자 - 목표 검증: 존재 여부, 같은 팀 여부 (임시)
        validMemberAndGoal(teamId, goalId);

        // 업데이트 실시: @Transactional
        boolean isRestore = goalTransactionalService.updateGoal(dto, goalId, teamId);
        if (!isRestore) {
            return null;
        } else {
            LocalDateTime now = LocalDateTime.now();
            return GoalConverter.toUpdateGoal(goalId, now);
        }
    }

    // 목표 삭제
    @Transactional
    public void deleteGoal(
            Long teamId,
            Long goalId
    ){

        // 사용자 - 목표 검증: 존재 여부, 같은 팀 여부
        validMemberAndGoal(teamId, goalId);

        // 삭제: 목표, 이슈, 담당자 / @Transactional
        goalTransactionalService.deleteGoal(goalId);
    }

    // 사용자 - 목표 검증: 존재 여부, 같은 팀 여부
    private void validMemberAndGoal(Long teamId, Long goalId) {

        // 삭제할 목표 존재 여부 검증
        Goal goal = goalRepository.findById(goalId).orElseThrow(() ->
                new GoalException(GoalErrorCode.NOT_FOUND));

        // 팀 존재 여부 검증
        if (!teamRepository.existsById(teamId)) {
            throw new TeamException(TeamErrorCode._NOT_FOUND);
        }

        // 팀원 여부 확인: 인증 객체 추출 (임시)
        MemberTeam member = memberTeamRepository.findByMemberIdAndTeamId(1L, teamId).orElseThrow(() ->
                new MemberHandler(MemberErrorStatus._FORBIDDEN));

        // 목표와 사용자가 같은 팀에 속하는지 검증
        if (!goal.getTeam().equals(member.getTeam())) {
            throw new GoalException(GoalErrorCode.FORBIDDEN);
        }
    }
}
