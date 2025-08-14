# VECO Backend
VECO는 **AI를 활용해 분산된 정보를 한곳에 모으고, 목표를 명확히 추적하며 협업을 효율화하는 시스템**입니다.

이 저장소는 VECO의 **백엔드 개발을 담당하는 레포지토리**입니다.

---
## ⚙️ 기슬 스택
- Java 21
- Spring 3.5.3
- Swagger 2.7.0
- MySQL
- QueryDSL (OpenFeign)
- Redis
- WebClient
- AWS S3
- Spring Security
---
## 🏗️ 프로젝트 구조
- 도메인 주도 설계 (DDD)
- main - develop 브랜치 구조: main = 배포 환경 / develop = 개발 환경
- 새로운 브랜치명은 #이슈번호-이슈제목 으로 통일
- 새로운 기능은 Feat/, 리팩토링은 Refactor/, 핫픽스는 Fix/로 통일

### 📋 커밋 메시지 컨벤션
|   Gitmoji   |    Tag     | Description |
|:-----------:|:----------:| --- |
|      ✨     |   `feat`   | 새로운 기능 추가 |
|     🐛      |   `fix`    | 버그 수정 |
|     📝      |   `docs`   | 문서 추가, 수정, 삭제 |
|     ✅      |   `test`   | 테스트 코드 추가, 수정, 삭제 |
|     💄      |  `style`   | 코드 형식 변경 |
|     ♻️      | `refactor` | 코드 리팩토링 |
|     ⚡️      |   `perf`   | 성능 개선 |
|     💚      |    `ci`    | CI 관련 설정 수정 |
|     🚀      |  `chore`   | 기타 변경사항 |
|     🔥️      |  `remove`  | 코드 및 파일 제거 |

---
## 👤 담당 도메인
- 이람/박승범: 외부이슈, Github 연동
- 나우/고낭연: 워크스페이스, 사용자 세팅
- 비니/문소빈: OAuth, 이슈
- 이영/이은영: 이슈, 알림
- 마크/김주헌: 목표, Slack 연동
---
## 🛜 서버 현황
<img width="776" height="567" alt="476164894-f109d3c6-85dd-4725-9ae0-59a8ef432026" src="https://github.com/user-attachments/assets/aa079cd7-2711-4502-9db8-69738e3ae9c0" />

- main-develop 브랜치 구조에 따라 배포환경, 개발환경을 따로 구성
- 개발 환경(localhost:5173)과 배포 환경(web.vecoservice.shop)간 테스트를 진행
- 추후, 서버를 통합 관리해 무중단 배포 전략(블루-그린) 사용

