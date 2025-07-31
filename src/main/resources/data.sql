-- 테스트 데이터 삽입 스크립트 (대량 데이터)

-- 워크스페이스 데이터
INSERT IGNORE INTO workspace (id, name, created_at, updated_at) VALUES 
(1, 'Test Workspace', NOW(), NOW());

-- 팀 데이터
INSERT IGNORE INTO team (id, name, workspace_id, created_at, updated_at) VALUES 
(1, 'Test Team', 1, NOW(), NOW());

-- 프로필 데이터 (먼저 생성)
INSERT IGNORE INTO profile (id, name, profile_image_url, created_at, updated_at) VALUES 
(1, '김개발', 'https://example.com/profile1.jpg', NOW(), NOW()),
(2, '이디자인', 'https://example.com/profile2.jpg', NOW(), NOW()),
(3, '박기획', 'https://example.com/profile3.jpg', NOW(), NOW()),
(4, '최테스트', 'https://example.com/profile4.jpg', NOW(), NOW()),
(5, '정코딩', 'https://example.com/profile5.jpg', NOW(), NOW());

-- 멤버 데이터
INSERT IGNORE INTO member (id, name, nickname, email, provider, profile_id, workspace_id, created_at, updated_at) VALUES 
(1, '김개발', '개발왕', 'dev@example.com', 'GOOGLE', 1, 1, NOW(), NOW()),
(2, '이디자인', '디자인킹', 'design@example.com', 'GOOGLE', 2, 1, NOW(), NOW()),
(3, '박기획', '기획신', 'plan@example.com', 'GOOGLE', 3, 1, NOW(), NOW()),
(4, '최테스트', '테스트마스터', 'test@example.com', 'GOOGLE', 4, 1, NOW(), NOW()),
(5, '정코딩', '코딩장인', 'coding@example.com', 'GOOGLE', 5, 1, NOW(), NOW());

-- 목표 데이터
INSERT IGNORE INTO goal (id, name, title, content, state, priority, deadline_start, deadline_end, team_id, created_at, updated_at) VALUES 
(1, 'MVP', 'Q1 MVP 개발', '1분기 MVP 버전 개발 완료', 'IN_PROGRESS', 'HIGH', '2025-01-01', '2025-03-31', 1, NOW(), NOW()),
(2, 'UX', 'UI/UX 개선', '사용자 경험 개선 프로젝트', 'TODO', 'HIGH', '2025-02-01', '2025-04-30', 1, NOW(), NOW()),
(3, 'Performance', '성능 최적화', '시스템 성능 개선', 'TODO', 'MEDIUM', '2025-03-01', '2025-05-31', 1, NOW(), NOW());

-- 외부 이슈 데이터 (50개 - 다양한 상태와 우선순위)
INSERT IGNORE INTO external (id, github_data_id, name, title, description, state, priority, start_date, end_date, service_type, external_code, team_id, goal_id, created_at, updated_at) VALUES 
-- NONE 상태 (10개)
(1, NULL, 'EXT-001', '회원가입 기능 구현', 'OAuth를 이용한 회원가입 기능을 구현합니다.', 'NONE', 'HIGH', '2025-01-15', '2025-02-15', 'GITHUB', 'EXT-001', 1, 1, DATE_SUB(NOW(), INTERVAL 10 DAY), NOW()),
(2, NULL, 'EXT-002', '로그인 UI 디자인', '사용자 친화적인 로그인 화면을 디자인합니다.', 'NONE', 'MEDIUM', '2025-01-20', '2025-02-20', 'SLACK', 'EXT-002', 1, 2, DATE_SUB(NOW(), INTERVAL 9 DAY), NOW()),
(3, NULL, 'EXT-003', '데이터베이스 스키마 설계', '효율적인 데이터베이스 구조를 설계합니다.', 'NONE', 'HIGH', '2025-01-25', '2025-02-25', 'NOTION', 'EXT-003', 1, 1, DATE_SUB(NOW(), INTERVAL 8 DAY), NOW()),
(4, NULL, 'EXT-004', '프로젝트 문서화', '개발 가이드라인 문서를 작성합니다.', 'NONE', 'LOW', '2025-01-30', '2025-03-01', 'NOTION', 'EXT-004', 1, 3, DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),
(5, NULL, 'EXT-005', 'CI/CD 파이프라인 구축', '자동화된 배포 시스템을 구축합니다.', 'NONE', 'MEDIUM', '2025-02-01', '2025-03-15', 'GITHUB', 'EXT-005', 1, 1, DATE_SUB(NOW(), INTERVAL 6 DAY), NOW()),
(6, NULL, 'EXT-006', '모바일 반응형 디자인', '모바일 환경에 최적화된 UI를 디자인합니다.', 'NONE', 'HIGH', '2025-02-05', '2025-03-20', 'SLACK', 'EXT-006', 1, 2, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
(7, NULL, 'EXT-007', '보안 검토', '애플리케이션 보안 취약점을 검토합니다.', 'NONE', 'HIGH', '2025-02-10', '2025-03-25', 'SLACK', 'EXT-007', 1, 1, DATE_SUB(NOW(), INTERVAL 4 DAY), NOW()),
(8, NULL, 'EXT-008', '성능 모니터링 설정', '시스템 성능 모니터링을 설정합니다.', 'NONE', 'MEDIUM', '2025-02-15', '2025-03-30', 'GITHUB', 'EXT-008', 1, 3, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
(9, NULL, 'EXT-009', '브랜딩 가이드라인', '일관된 브랜드 아이덴티티를 정립합니다.', 'NONE', 'LOW', '2025-02-20', '2025-04-01', 'SLACK', 'EXT-009', 1, 2, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(10, NULL, 'EXT-010', '사용자 피드백 수집', '베타 사용자 피드백을 수집하고 분석합니다.', 'NONE', 'MEDIUM', '2025-02-25', '2025-04-05', 'NOTION', 'EXT-010', 1, 2, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),

-- TODO 상태 (15개)
(11, NULL, 'EXT-011', 'REST API 개발', '백엔드 REST API를 개발합니다.', 'TODO', 'HIGH', '2025-01-30', '2025-03-01', 'GITHUB', 'EXT-011', 1, 1, DATE_SUB(NOW(), INTERVAL 15 DAY), NOW()),
(12, NULL, 'EXT-012', '프론트엔드 컴포넌트 개발', '재사용 가능한 UI 컴포넌트를 개발합니다.', 'TODO', 'HIGH', '2025-02-01', '2025-03-15', 'GITHUB', 'EXT-012', 1, 2, DATE_SUB(NOW(), INTERVAL 14 DAY), NOW()),
(13, NULL, 'EXT-013', '데이터 마이그레이션', '기존 데이터를 새로운 스키마로 마이그레이션합니다.', 'TODO', 'MEDIUM', '2025-02-05', '2025-03-20', 'GITHUB', 'EXT-013', 1, 1, DATE_SUB(NOW(), INTERVAL 13 DAY), NOW()),
(14, NULL, 'EXT-014', '검색 기능 구현', '전체 텍스트 검색 기능을 구현합니다.', 'TODO', 'HIGH', '2025-02-10', '2025-03-25', 'GITHUB', 'EXT-014', 1, 1, DATE_SUB(NOW(), INTERVAL 12 DAY), NOW()),
(15, NULL, 'EXT-015', '알림 시스템 설계', '실시간 알림 시스템을 설계합니다.', 'TODO', 'MEDIUM', '2025-02-15', '2025-03-30', 'SLACK', 'EXT-015', 1, 1, DATE_SUB(NOW(), INTERVAL 11 DAY), NOW()),
(16, NULL, 'EXT-016', '사용자 권한 관리', '역할 기반 접근 제어를 구현합니다.', 'TODO', 'HIGH', '2025-02-20', '2025-04-01', 'GITHUB', 'EXT-016', 1, 1, DATE_SUB(NOW(), INTERVAL 10 DAY), NOW()),
(17, NULL, 'EXT-017', '파일 업로드 기능', '이미지 및 파일 업로드 기능을 구현합니다.', 'TODO', 'MEDIUM', '2025-02-25', '2025-04-05', 'GITHUB', 'EXT-017', 1, 1, DATE_SUB(NOW(), INTERVAL 9 DAY), NOW()),
(18, NULL, 'EXT-018', '대시보드 설계', '관리자용 대시보드를 설계합니다.', 'TODO', 'HIGH', '2025-03-01', '2025-04-10', 'SLACK', 'EXT-018', 1, 2, DATE_SUB(NOW(), INTERVAL 8 DAY), NOW()),
(19, NULL, 'EXT-019', '캐싱 전략 수립', '애플리케이션 성능 향상을 위한 캐싱 전략을 수립합니다.', 'TODO', 'MEDIUM', '2025-03-05', '2025-04-15', 'GITHUB', 'EXT-019', 1, 3, DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),
(20, NULL, 'EXT-020', '에러 로깅 시스템', '체계적인 에러 로깅 시스템을 구축합니다.', 'TODO', 'LOW', '2025-03-10', '2025-04-20', 'SLACK', 'EXT-020', 1, 3, DATE_SUB(NOW(), INTERVAL 6 DAY), NOW()),
(21, NULL, 'EXT-021', '소셜 로그인 연동', '구글, 카카오, 네이버 소셜 로그인을 연동합니다.', 'TODO', 'HIGH', '2025-03-15', '2025-04-25', 'GITHUB', 'EXT-021', 1, 1, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
(22, NULL, 'EXT-022', '다국어 지원', '한국어, 영어 다국어 지원을 구현합니다.', 'TODO', 'LOW', '2025-03-20', '2025-04-30', 'GITHUB', 'EXT-022', 1, 2, DATE_SUB(NOW(), INTERVAL 4 DAY), NOW()),
(23, NULL, 'EXT-023', '이메일 템플릿 디자인', '시스템 이메일 템플릿을 디자인합니다.', 'TODO', 'MEDIUM', '2025-03-25', '2025-05-01', 'SLACK', 'EXT-023', 1, 2, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
(24, NULL, 'EXT-024', 'PDF 리포트 생성', '데이터 리포트 PDF 생성 기능을 구현합니다.', 'TODO', 'LOW', '2025-03-30', '2025-05-05', 'GITHUB', 'EXT-024', 1, 3, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(25, NULL, 'EXT-025', '백업 시스템 구축', '데이터 백업 및 복구 시스템을 구축합니다.', 'TODO', 'HIGH', '2025-04-01', '2025-05-10', 'GITHUB', 'EXT-025', 1, 3, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),

-- IN_PROGRESS 상태 (12개)
(26, NULL, 'EXT-026', '사용자 인증 개발', 'JWT 기반 사용자 인증을 개발 중입니다.', 'IN_PROGRESS', 'HIGH', '2025-01-20', '2025-02-28', 'GITHUB', 'EXT-026', 1, 1, DATE_SUB(NOW(), INTERVAL 20 DAY), NOW()),
(27, NULL, 'EXT-027', '메인 페이지 개발', '메인 페이지 UI와 기능을 개발 중입니다.', 'IN_PROGRESS', 'HIGH', '2025-01-25', '2025-03-05', 'GITHUB', 'EXT-027', 1, 2, DATE_SUB(NOW(), INTERVAL 19 DAY), NOW()),
(28, NULL, 'EXT-028', '데이터 시각화', '차트와 그래프로 데이터를 시각화하고 있습니다.', 'IN_PROGRESS', 'MEDIUM', '2025-02-01', '2025-03-10', 'GITHUB', 'EXT-028', 1, 2, DATE_SUB(NOW(), INTERVAL 18 DAY), NOW()),
(29, NULL, 'EXT-029', '테스트 코드 작성', '단위 테스트와 통합 테스트를 작성 중입니다.', 'IN_PROGRESS', 'HIGH', '2025-02-05', '2025-03-15', 'GITHUB', 'EXT-029', 1, 1, DATE_SUB(NOW(), INTERVAL 17 DAY), NOW()),
(30, NULL, 'EXT-030', '성능 최적화', '데이터베이스 쿼리 최적화를 진행 중입니다.', 'IN_PROGRESS', 'MEDIUM', '2025-02-10', '2025-03-20', 'GITHUB', 'EXT-030', 1, 3, DATE_SUB(NOW(), INTERVAL 16 DAY), NOW()),
(31, NULL, 'EXT-031', '반응형 웹 구현', '모바일 친화적인 반응형 웹을 구현 중입니다.', 'IN_PROGRESS', 'HIGH', '2025-02-15', '2025-03-25', 'GITHUB', 'EXT-031', 1, 2, DATE_SUB(NOW(), INTERVAL 15 DAY), NOW()),
(32, NULL, 'EXT-032', 'WebSocket 구현', '실시간 통신을 위한 WebSocket을 구현 중입니다.', 'IN_PROGRESS', 'MEDIUM', '2025-02-20', '2025-03-30', 'GITHUB', 'EXT-032', 1, 1, DATE_SUB(NOW(), INTERVAL 14 DAY), NOW()),
(33, NULL, 'EXT-033', '이미지 최적화', '이미지 압축 및 CDN 연동을 진행 중입니다.', 'IN_PROGRESS', 'LOW', '2025-02-25', '2025-04-01', 'GITHUB', 'EXT-033', 1, 3, DATE_SUB(NOW(), INTERVAL 13 DAY), NOW()),
(34, NULL, 'EXT-034', 'API 문서화', 'Swagger를 이용한 API 문서화를 진행 중입니다.', 'IN_PROGRESS', 'MEDIUM', '2025-03-01', '2025-04-05', 'GITHUB', 'EXT-034', 1, 1, DATE_SUB(NOW(), INTERVAL 12 DAY), NOW()),
(35, NULL, 'EXT-035', '로그 분석 시스템', 'ELK 스택을 이용한 로그 분석 시스템을 구축 중입니다.', 'IN_PROGRESS', 'LOW', '2025-03-05', '2025-04-10', 'GITHUB', 'EXT-035', 1, 3, DATE_SUB(NOW(), INTERVAL 11 DAY), NOW()),
(36, NULL, 'EXT-036', '접근성 개선', '웹 접근성 가이드라인을 준수하여 개선 중입니다.', 'IN_PROGRESS', 'MEDIUM', '2025-03-10', '2025-04-15', 'GITHUB', 'EXT-036', 1, 2, DATE_SUB(NOW(), INTERVAL 10 DAY), NOW()),
(37, NULL, 'EXT-037', '부하 테스트', 'JMeter를 이용한 부하 테스트를 진행 중입니다.', 'IN_PROGRESS', 'HIGH', '2025-03-15', '2025-04-20', 'GITHUB', 'EXT-037', 1, 3, DATE_SUB(NOW(), INTERVAL 9 DAY), NOW()),

-- REVIEW 상태 (8개)
(38, NULL, 'EXT-038', '코드 리뷰', '신규 기능에 대한 코드 리뷰가 진행 중입니다.', 'REVIEW', 'HIGH', '2025-02-01', '2025-02-15', 'GITHUB', 'EXT-038', 1, 1, DATE_SUB(NOW(), INTERVAL 25 DAY), NOW()),
(39, NULL, 'EXT-039', '디자인 검토', 'UI/UX 디자인에 대한 검토가 진행 중입니다.', 'REVIEW', 'HIGH', '2025-02-05', '2025-02-20', 'SLACK', 'EXT-039', 1, 2, DATE_SUB(NOW(), INTERVAL 24 DAY), NOW()),
(40, NULL, 'EXT-040', '보안 감사', '보안 전문가의 보안 감사가 진행 중입니다.', 'REVIEW', 'HIGH', '2025-02-10', '2025-02-25', 'GITHUB', 'EXT-040', 1, 1, DATE_SUB(NOW(), INTERVAL 23 DAY), NOW()),
(41, NULL, 'EXT-041', '성능 검토', '시스템 성능에 대한 검토가 진행 중입니다.', 'REVIEW', 'MEDIUM', '2025-02-15', '2025-03-01', 'GITHUB', 'EXT-041', 1, 3, DATE_SUB(NOW(), INTERVAL 22 DAY), NOW()),
(42, NULL, 'EXT-042', '사용성 테스트', '사용자 경험에 대한 사용성 테스트가 진행 중입니다.', 'REVIEW', 'MEDIUM', '2025-02-20', '2025-03-05', 'SLACK', 'EXT-042', 1, 2, DATE_SUB(NOW(), INTERVAL 21 DAY), NOW()),
(43, NULL, 'EXT-043', '품질 보증', 'QA 팀의 품질 보증 테스트가 진행 중입니다.', 'REVIEW', 'HIGH', '2025-02-25', '2025-03-10', 'SLACK', 'EXT-043', 1, 1, DATE_SUB(NOW(), INTERVAL 20 DAY), NOW()),
(44, NULL, 'EXT-044', '문서 검토', '기술 문서에 대한 검토가 진행 중입니다.', 'REVIEW', 'LOW', '2025-03-01', '2025-03-15', 'NOTION', 'EXT-044', 1, 3, DATE_SUB(NOW(), INTERVAL 19 DAY), NOW()),
(45, NULL, 'EXT-045', '배포 승인', '프로덕션 배포에 대한 승인 검토가 진행 중입니다.', 'REVIEW', 'HIGH', '2025-03-05', '2025-03-20', 'GITHUB', 'EXT-045', 1, 1, DATE_SUB(NOW(), INTERVAL 18 DAY), NOW()),

-- FINISH 상태 (5개)
(46, NULL, 'EXT-046', '초기 설정 완료', '프로젝트 초기 환경 설정이 완료되었습니다.', 'FINISH', 'HIGH', '2025-01-01', '2025-01-15', 'GITHUB', 'EXT-046', 1, 1, DATE_SUB(NOW(), INTERVAL 30 DAY), NOW()),
(47, NULL, 'EXT-047', '브랜드 로고 완성', '회사 브랜드 로고 디자인이 완성되었습니다.', 'FINISH', 'MEDIUM', '2025-01-05', '2025-01-20', 'SLACK', 'EXT-047', 1, 2, DATE_SUB(NOW(), INTERVAL 29 DAY), NOW()),
(48, NULL, 'EXT-048', '도메인 구매', '서비스 도메인 구매가 완료되었습니다.', 'FINISH', 'LOW', '2025-01-10', '2025-01-25', 'NOTION', 'EXT-048', 1, 3, DATE_SUB(NOW(), INTERVAL 28 DAY), NOW()),
(49, NULL, 'EXT-049', '서버 인프라 구축', 'AWS 클라우드 인프라 구축이 완료되었습니다.', 'FINISH', 'HIGH', '2025-01-15', '2025-01-30', 'GITHUB', 'EXT-049', 1, 1, DATE_SUB(NOW(), INTERVAL 27 DAY), NOW()),
(50, NULL, 'EXT-050', '팀 협업 도구 설정', 'Slack, Jira 등 협업 도구 설정이 완료되었습니다.', 'FINISH', 'MEDIUM', '2025-01-20', '2025-02-01', 'SLACK', 'EXT-050', 1, 3, DATE_SUB(NOW(), INTERVAL 26 DAY), NOW());

-- Assignment 데이터 (각 이슈에 1-3명의 담당자 랜덤 배정)
INSERT IGNORE INTO assignment (id, category, assignee_name, profile_url, assignee_id, external_id, created_at, updated_at) VALUES 
-- NONE 상태 이슈들 (1-10)
(1, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 1, NOW(), NOW()),
(2, 'EXTERNAL', '이디자인', 'https://example.com/profile2.jpg', 2, 2, NOW(), NOW()),
(3, 'EXTERNAL', '박기획', 'https://example.com/profile3.jpg', 3, 3, NOW(), NOW()),
(4, 'EXTERNAL', '최테스트', 'https://example.com/profile4.jpg', 4, 4, NOW(), NOW()),
(5, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 5, NOW(), NOW()),
(6, 'EXTERNAL', '정코딩', 'https://example.com/profile5.jpg', 5, 5, NOW(), NOW()),
(7, 'EXTERNAL', '이디자인', 'https://example.com/profile2.jpg', 2, 6, NOW(), NOW()),
(8, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 7, NOW(), NOW()),
(9, 'EXTERNAL', '박기획', 'https://example.com/profile3.jpg', 3, 7, NOW(), NOW()),
(10, 'EXTERNAL', '최테스트', 'https://example.com/profile4.jpg', 4, 8, NOW(), NOW()),
(11, 'EXTERNAL', '이디자인', 'https://example.com/profile2.jpg', 2, 9, NOW(), NOW()),
(12, 'EXTERNAL', '정코딩', 'https://example.com/profile5.jpg', 5, 10, NOW(), NOW()),

-- TODO 상태 이슈들 (11-25)
(13, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 11, NOW(), NOW()),
(14, 'EXTERNAL', '정코딩', 'https://example.com/profile5.jpg', 5, 11, NOW(), NOW()),
(15, 'EXTERNAL', '이디자인', 'https://example.com/profile2.jpg', 2, 12, NOW(), NOW()),
(16, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 13, NOW(), NOW()),
(17, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 14, NOW(), NOW()),
(18, 'EXTERNAL', '박기획', 'https://example.com/profile3.jpg', 3, 14, NOW(), NOW()),
(19, 'EXTERNAL', '정코딩', 'https://example.com/profile5.jpg', 5, 15, NOW(), NOW()),
(20, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 16, NOW(), NOW()),
(21, 'EXTERNAL', '최테스트', 'https://example.com/profile4.jpg', 4, 16, NOW(), NOW()),
(22, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 17, NOW(), NOW()),
(23, 'EXTERNAL', '이디자인', 'https://example.com/profile2.jpg', 2, 18, NOW(), NOW()),
(24, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 19, NOW(), NOW()),
(25, 'EXTERNAL', '정코딩', 'https://example.com/profile5.jpg', 5, 20, NOW(), NOW()),
(26, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 21, NOW(), NOW()),
(27, 'EXTERNAL', '이디자인', 'https://example.com/profile2.jpg', 2, 22, NOW(), NOW()),
(28, 'EXTERNAL', '이디자인', 'https://example.com/profile2.jpg', 2, 23, NOW(), NOW()),
(29, 'EXTERNAL', '정코딩', 'https://example.com/profile5.jpg', 5, 24, NOW(), NOW()),
(30, 'EXTERNAL', '최테스트', 'https://example.com/profile4.jpg', 4, 25, NOW(), NOW()),

-- IN_PROGRESS 상태 이슈들 (26-37)
(31, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 26, NOW(), NOW()),
(32, 'EXTERNAL', '이디자인', 'https://example.com/profile2.jpg', 2, 27, NOW(), NOW()),
(33, 'EXTERNAL', '정코딩', 'https://example.com/profile5.jpg', 5, 27, NOW(), NOW()),
(34, 'EXTERNAL', '이디자인', 'https://example.com/profile2.jpg', 2, 28, NOW(), NOW()),
(35, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 29, NOW(), NOW()),
(36, 'EXTERNAL', '최테스트', 'https://example.com/profile4.jpg', 4, 29, NOW(), NOW()),
(37, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 30, NOW(), NOW()),
(38, 'EXTERNAL', '이디자인', 'https://example.com/profile2.jpg', 2, 31, NOW(), NOW()),
(39, 'EXTERNAL', '정코딩', 'https://example.com/profile5.jpg', 5, 32, NOW(), NOW()),
(40, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 33, NOW(), NOW()),
(41, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 34, NOW(), NOW()),
(42, 'EXTERNAL', '박기획', 'https://example.com/profile3.jpg', 3, 34, NOW(), NOW()),
(43, 'EXTERNAL', '정코딩', 'https://example.com/profile5.jpg', 5, 35, NOW(), NOW()),
(44, 'EXTERNAL', '이디자인', 'https://example.com/profile2.jpg', 2, 36, NOW(), NOW()),
(45, 'EXTERNAL', '최테스트', 'https://example.com/profile4.jpg', 4, 37, NOW(), NOW()),

-- REVIEW 상태 이슈들 (38-45)
(46, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 38, NOW(), NOW()),
(47, 'EXTERNAL', '박기획', 'https://example.com/profile3.jpg', 3, 38, NOW(), NOW()),
(48, 'EXTERNAL', '이디자인', 'https://example.com/profile2.jpg', 2, 39, NOW(), NOW()),
(49, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 40, NOW(), NOW()),
(50, 'EXTERNAL', '최테스트', 'https://example.com/profile4.jpg', 4, 40, NOW(), NOW()),
(51, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 41, NOW(), NOW()),
(52, 'EXTERNAL', '이디자인', 'https://example.com/profile2.jpg', 2, 42, NOW(), NOW()),
(53, 'EXTERNAL', '최테스트', 'https://example.com/profile4.jpg', 4, 43, NOW(), NOW()),
(54, 'EXTERNAL', '박기획', 'https://example.com/profile3.jpg', 3, 44, NOW(), NOW()),
(55, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 45, NOW(), NOW()),
(56, 'EXTERNAL', '정코딩', 'https://example.com/profile5.jpg', 5, 45, NOW(), NOW()),

-- FINISH 상태 이슈들 (46-50)
(57, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 46, NOW(), NOW()),
(58, 'EXTERNAL', '이디자인', 'https://example.com/profile2.jpg', 2, 47, NOW(), NOW()),
(59, 'EXTERNAL', '박기획', 'https://example.com/profile3.jpg', 3, 48, NOW(), NOW()),
(60, 'EXTERNAL', '김개발', 'https://example.com/profile1.jpg', 1, 49, NOW(), NOW()),
(61, 'EXTERNAL', '최테스트', 'https://example.com/profile4.jpg', 4, 49, NOW(), NOW()),
(62, 'EXTERNAL', '박기획', 'https://example.com/profile3.jpg', 3, 50, NOW(), NOW());