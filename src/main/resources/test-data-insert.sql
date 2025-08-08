-- 테스트 데이터 삽입 스크립트
-- 외부 이슈 그룹핑 페이지네이션 테스트를 위한 다양한 데이터

-- 1. Member 테스트 데이터 (담당자용)
INSERT INTO member (name, nickname, email, provider, role, created_at, updated_at) VALUES
('김철수', 'chulsoo', 'chulsoo@test.com', 'GOOGLE', 'USER', NOW(), NOW()),
('이영희', 'younghee', 'younghee@test.com', 'GOOGLE', 'USER', NOW(), NOW()),  
('박민수', 'minsu', 'minsu@test.com', 'GOOGLE', 'USER', NOW(), NOW()),
('최지혜', 'jihye', 'jihye@test.com', 'GOOGLE', 'USER', NOW(), NOW());

-- 2. Team 테스트 데이터
INSERT INTO team (name, info, team_image, invite_code, created_at, updated_at) VALUES
('테스트팀', '테스트용 팀입니다', null, 'TEST123', NOW(), NOW());

-- 3. Goal 테스트 데이터 (목표)
INSERT INTO goal (name, title, content, state, priority, deadline_start, deadline_end, team_id, created_at, updated_at) VALUES
('프론트엔드 개발', '웹 프론트엔드 완성하기', '리액트 기반 프론트엔드 개발', 'IN_PROGRESS', 'HIGH', '2024-01-01', '2024-03-31', 1, NOW(), NOW()),
('백엔드 API', 'REST API 개발', '스프링부트 백엔드 API 구현', 'TODO', 'URGENT', '2024-02-01', '2024-04-30', 1, NOW(), NOW()),
('데이터베이스 설계', 'DB 스키마 설계', '데이터베이스 스키마 및 관계 설정', 'FINISH', 'NORMAL', '2023-12-01', '2024-01-31', 1, NOW(), NOW()),
('DevOps 구축', 'CI/CD 파이프라인', '자동화 배포 시스템 구축', 'NONE', 'LOW', null, null, 1, NOW(), NOW());

-- 4. External 테스트 데이터 (다양한 조합)
INSERT INTO external (github_data_id, name, title, description, state, member_id, priority, start_date, end_date, service_type, team_id, goal_id, created_at, updated_at) VALUES
-- 상태별 다양한 이슈들
(1001, 'EXT-001', '로그인 페이지 개발', 'OAuth 로그인 페이지 구현', 'NONE', 1, 'URGENT', '2024-01-15', '2024-01-30', 'GITHUB', 1, 1, NOW() - INTERVAL 20 DAY, NOW()),
(1002, 'EXT-002', '회원가입 API', '사용자 등록 REST API 개발', 'TODO', 1, 'HIGH', '2024-01-20', '2024-02-10', 'GITHUB', 1, 2, NOW() - INTERVAL 18 DAY, NOW()),
(1003, 'EXT-003', '데이터베이스 스키마', 'User 테이블 스키마 설계', 'IN_PROGRESS', 2, 'HIGH', '2024-01-10', '2024-01-25', 'JIRA', 1, 3, NOW() - INTERVAL 15 DAY, NOW()),
(1004, 'EXT-004', '비밀번호 암호화', 'BCrypt 암호화 적용', 'FINISH', 2, 'NORMAL', '2024-01-05', '2024-01-20', 'GITHUB', 1, 2, NOW() - INTERVAL 12 DAY, NOW()),
(1005, 'EXT-005', 'JWT 토큰 관리', 'JWT 인증 시스템 구현', 'REVIEW', 3, 'URGENT', '2024-01-25', '2024-02-15', 'NOTION', 1, 2, NOW() - INTERVAL 10 DAY, NOW()),

-- 우선순위별 다양한 이슈들  
(1006, 'EXT-006', '메인 대시보드 UI', '메인 화면 컴포넌트 개발', 'TODO', 1, 'NONE', null, null, 'GITHUB', 1, 1, NOW() - INTERVAL 8 DAY, NOW()),
(1007, 'EXT-007', '보안 취약점 점검', 'OWASP 보안 점검', 'IN_PROGRESS', 3, 'URGENT', '2024-02-01', '2024-02-10', 'JIRA', 1, null, NOW() - INTERVAL 6 DAY, NOW()),
(1008, 'EXT-008', '성능 최적화', 'DB 쿼리 최적화', 'NONE', 4, 'LOW', null, null, 'SLACK', 1, 3, NOW() - INTERVAL 4 DAY, NOW()),
(1009, 'EXT-009', '단위 테스트 작성', 'Service 레이어 테스트', 'TODO', 2, 'NORMAL', '2024-02-05', '2024-02-20', 'GITHUB', 1, 2, NOW() - INTERVAL 2 DAY, NOW()),
(1010, 'EXT-010', '문서화 작업', 'API 문서 Swagger 적용', 'REVIEW', 4, 'HIGH', '2024-01-30', '2024-02-15', 'NOTION', 1, null, NOW() - INTERVAL 1 DAY, NOW()),

-- 서비스 타입별 다양한 이슈들
(1011, 'EXT-011', '깃허브 이슈 연동', 'GitHub Issues 자동 연동', 'FINISH', 1, 'HIGH', '2024-01-01', '2024-01-15', 'GITHUB', 1, 4, NOW() - INTERVAL 25 DAY, NOW()),
(1012, 'EXT-012', '지라 티켓 동기화', 'JIRA 티켓 연동 시스템', 'IN_PROGRESS', 2, 'NORMAL', '2024-01-10', '2024-02-28', 'JIRA', 1, 4, NOW() - INTERVAL 14 DAY, NOW()),
(1013, 'EXT-013', '슬랙 알림 설정', '이슈 상태 변경 시 슬랙 알림', 'TODO', 3, 'LOW', '2024-02-10', null, 'SLACK', 1, 4, NOW() - INTERVAL 3 DAY, NOW()),
(1014, 'EXT-014', '노션 페이지 연동', '프로젝트 문서 자동 업데이트', 'NONE', 4, 'NORMAL', null, null, 'NOTION', 1, 1, NOW(), NOW()),

-- 목표 없는 이슈들 (goal_id = null)
(1015, 'EXT-015', '임시 버그 수정', '긴급 버그 핫픽스', 'URGENT', 1, 'URGENT', '2024-02-08', '2024-02-08', 'GITHUB', 1, null, NOW() - INTERVAL 5 DAY, NOW()),
(1016, 'EXT-016', '서버 모니터링', '서버 상태 모니터링 설정', 'IN_PROGRESS', 3, 'HIGH', '2024-02-01', '2024-02-29', 'SLACK', 1, null, NOW() - INTERVAL 7 DAY, NOW());

-- 5. Assignment 테스트 데이터 (담당자 배정) - 복수 담당자 포함
INSERT INTO assignment (category, assignee_name, profile_url, assignee_id, external_id, created_at, updated_at) VALUES
-- EXT-001: 김철수
('EXTERNAL', '김철수', null, 1, 1, NOW(), NOW()),
-- EXT-002: 김철수 + 이영희 (복수 담당자)
('EXTERNAL', '김철수', null, 1, 2, NOW(), NOW()),
('EXTERNAL', '이영희', null, 2, 2, NOW(), NOW()),
-- EXT-003: 이영희
('EXTERNAL', '이영희', null, 2, 3, NOW(), NOW()),
-- EXT-004: 이영희 + 박민수 (복수 담당자)
('EXTERNAL', '이영희', null, 2, 4, NOW(), NOW()),
('EXTERNAL', '박민수', null, 3, 4, NOW(), NOW()),
-- EXT-005: 박민수
('EXTERNAL', '박민수', null, 3, 5, NOW(), NOW()),
-- EXT-006: 담당자 없음 (assignment 없음)
-- EXT-007: 박민수 + 최지혜 (복수 담당자)
('EXTERNAL', '박민수', null, 3, 7, NOW(), NOW()),
('EXTERNAL', '최지혜', null, 4, 7, NOW(), NOW()),
-- EXT-008: 최지혜
('EXTERNAL', '최지혜', null, 4, 8, NOW(), NOW()),
-- EXT-009: 이영희 + 최지혜 (복수 담당자)  
('EXTERNAL', '이영희', null, 2, 9, NOW(), NOW()),
('EXTERNAL', '최지혜', null, 4, 9, NOW(), NOW()),
-- EXT-010: 최지혜
('EXTERNAL', '최지혜', null, 4, 10, NOW(), NOW()),
-- EXT-011: 김철수
('EXTERNAL', '김철수', null, 1, 11, NOW(), NOW()),
-- EXT-012: 이영희 + 박민수 (복수 담당자)
('EXTERNAL', '이영희', null, 2, 12, NOW(), NOW()),
('EXTERNAL', '박민수', null, 3, 12, NOW(), NOW()),
-- EXT-013: 박민수
('EXTERNAL', '박민수', null, 3, 13, NOW(), NOW()),
-- EXT-014: 담당자 없음 (assignment 없음)
-- EXT-015: 김철수 + 이영희 + 박민수 (3명 담당자)
('EXTERNAL', '김철수', null, 1, 15, NOW(), NOW()),
('EXTERNAL', '이영희', null, 2, 15, NOW(), NOW()),
('EXTERNAL', '박민수', null, 3, 15, NOW(), NOW()),
-- EXT-016: 박민수
('EXTERNAL', '박민수', null, 3, 16, NOW(), NOW());