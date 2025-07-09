package com.example.Veco.domain.goal.service.command;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.assignee.entity.AssigneeConverter;
import com.example.Veco.domain.assignee.entity.AssigneeRepository;
import com.example.Veco.domain.common.entity.Image;
import com.example.Veco.domain.common.repository.ImageRepository;
import com.example.Veco.domain.goal.converter.GoalConverter;
import com.example.Veco.domain.goal.dto.request.GoalReqDTO;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.CreateGoal;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.issue.entity.IssueException;
import com.example.Veco.domain.issue.entity.IssueRepository;
import com.example.Veco.domain.mapping.MemberTeam;
import com.example.Veco.domain.mapping.MemberTeamRepository;
import com.example.Veco.domain.member.MemberException;
import com.example.Veco.domain.member.MemberRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.entity.TeamException;
import com.example.Veco.domain.team.entity.TeamRepository;
import com.example.Veco.global.aws.util.S3Util;
import com.example.Veco.global.enums.Category;
import com.example.Veco.global.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
            GoalReqDTO.CreateGoal dto
    ) {
        // 담당자 존재 여부, 같은 팀 여부 검증 + 본인 포함 여부 확인 후 업데이트
        List<Long> memberIds = new ArrayList<>(dto.managersId());
        if (dto.isIncludeMe()) {
            // 인증 객체에서 가져오기
            memberIds.add(1L);
        }

        // 사용자 존재 여부 검증
        List<Member> memberList = memberRepository.findAllById(memberIds);
        if (memberList.size() != memberIds.size()) {
            throw new MemberException("사용자 중 존재하지 않은 사용자가 있습니다.");
        }

        // 팀 존재 여부 검증
        if (!teamRepository.existsById(teamId)) {
            throw new TeamException("해당 팀이 존재하지 않습니다.");
        }

        // 같은 팀원 여부 검증
        List<MemberTeam> memberTeamList = memberTeamRepository.findAllByMemberIdAndTeamId(memberIds, teamId);
        if (memberTeamList.size() != memberIds.size()) {
            throw new MemberException("담당자 중 같은 팀원이 아닌 사용자가 있습니다.");
        }

        // 이슈 존재 여부 검증
        List<Issue> issueList = issueRepository.findAllById(dto.issueId());
        if (issueList.size() != dto.issueId().size()) {
            throw new IssueException("존재하지 않은 이슈가 있습니다.");
        }

        // 목표 생성: DTO, Team, Name 필요, @Transactional
        // 목표 이름(Veco-g3) 생성 로직: 분산 락 걸고 이름 조회, +1
        RLock lock = redissonClient.getLock("lock:goal:" + teamId);
        Long goalId;
        try {
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);
            if (!available) {
                throw new RuntimeException("redis Timeout");
            }
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

    // 목표 삭제
}
