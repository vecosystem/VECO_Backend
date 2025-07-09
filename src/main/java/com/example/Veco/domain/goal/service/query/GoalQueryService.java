package com.example.Veco.domain.goal.service.query;

import com.example.Veco.domain.goal.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalQueryService {

    private final GoalRepository goalRepository;

    // 팀 내 모든 목표 조회
    // 목표 상세 조회
    // 팀원 조회
}
