# GitHub 연동 배포 수정 방안

## 1. application.yml 환경별 설정 분리

### application-dev.yml (개발환경)
```yaml
cors:
  allowed-origins:
    - "http://localhost:3000"
    - "http://localhost:3001"

github:
  oauth:
    client-id: ${GITHUB_CLIENT_ID}
    client-secret: ${GITHUB_CLIENT_SECRET}
    redirect-uri: http://localhost:8080/github/oauth/callback
  app:
    id: ${GITHUB_APP_ID}
    private-key-path: ${GITHUB_PRIVATE_KEY_PATH}

frontend:
  base-url: http://localhost:3000
```

### application-prod.yml (운영환경)
```yaml
cors:
  allowed-origins:
    - "https://yourdomain.com"
    - "https://www.yourdomain.com"

github:
  oauth:
    client-id: ${GITHUB_CLIENT_ID}
    client-secret: ${GITHUB_CLIENT_SECRET}
    redirect-uri: https://api.yourdomain.com/github/oauth/callback
  app:
    id: ${GITHUB_APP_ID}
    private-key-path: ${GITHUB_PRIVATE_KEY_PATH}

frontend:
  base-url: https://yourdomain.com
```

## 2. GitHubController 수정

```java
@Controller
@RequiredArgsConstructor
@RequestMapping("/github")
@Slf4j
public class GitHubController {

    private final GitHubService gitHubService;
    private final GitHubConfig gitHubConfig;
    
    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @GetMapping("/connect")
    public String connectGithub(@RequestParam("teamId") Long teamId,
                               @RequestParam(value = "returnUrl", required = false) String returnUrl) {
        
        // state에 teamId와 returnUrl을 JSON으로 인코딩
        String state = encodeState(teamId, returnUrl != null ? returnUrl : "/dashboard");
        
        String authUrl = String.format(
                "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&scope=%s&state=%s",
                gitHubConfig.getOauth().getClientId(),
                gitHubConfig.getOauth().getRedirectUri(),
                "read:user,repo,admin:repo_hook",
                state
        );

        return "redirect:" + authUrl;
    }

    @GetMapping("/oauth/callback")
    public String githubOAuthCallback(@RequestParam("code") String code,
                                    @RequestParam("state") String state,
                                    Model model) {
        try {
            StateInfo stateInfo = decodeState(state);
            
            // GitHub Access Token 획득 및 사용자 정보 저장
            gitHubService.connectGitHubAccount(code, stateInfo.getTeamId());

            // GitHub App 설치 페이지로 리다이렉트 (state에 returnUrl 포함)
            String githubAppInstallUrl = String.format(
                    "https://github.com/apps/VecoApp/installations/new?state=%s",
                    encodeAppState(stateInfo.getTeamId(), stateInfo.getReturnUrl())
            );

            return "redirect:" + githubAppInstallUrl;

        } catch (Exception e) {
            log.error("GitHub 연동 중 오류 발생", e);
            return "redirect:" + frontendBaseUrl + "/error?message=" + 
                   URLEncoder.encode("GitHub 연동 중 오류가 발생했습니다.", StandardCharsets.UTF_8);
        }
    }

    @GetMapping("/app/callback")
    public String githubAppCallback(@RequestParam("installation_id") String installationId,
                                  @RequestParam("state") String state) {
        try {
            AppStateInfo stateInfo = decodeAppState(state);
            
            // GitHub App 설치 정보 저장
            gitHubService.saveGitHubAppInstallation(installationId, stateInfo.getTeamId());
            
            // 프론트엔드 성공 페이지로 리다이렉트
            String successUrl = String.format("%s%s?success=true&teamId=%d", 
                    frontendBaseUrl, 
                    stateInfo.getReturnUrl(),
                    stateInfo.getTeamId());
            
            return "redirect:" + successUrl;
            
        } catch (Exception e) {
            log.error("GitHub App 설치 중 오류 발생", e);
            return "redirect:" + frontendBaseUrl + "/error?message=" + 
                   URLEncoder.encode("GitHub App 설치 중 오류가 발생했습니다.", StandardCharsets.UTF_8);
        }
    }

    // Helper methods for state encoding/decoding
    private String encodeState(Long teamId, String returnUrl) {
        // JSON으로 인코딩 후 Base64 인코딩
        StateInfo stateInfo = new StateInfo(teamId, returnUrl);
        String json = objectMapper.writeValueAsString(stateInfo);
        return Base64.getEncoder().encodeToString(json.getBytes());
    }

    private StateInfo decodeState(String state) {
        // Base64 디코딩 후 JSON 파싱
        String json = new String(Base64.getDecoder().decode(state));
        return objectMapper.readValue(json, StateInfo.class);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class StateInfo {
        private Long teamId;
        private String returnUrl;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class AppStateInfo {
        private Long teamId;
        private String returnUrl;
    }
}
```

## 3. GitHub App Webhook 설정 추가

```java
@RestController
@RequestMapping("/github/webhook")
@RequiredArgsConstructor
@Slf4j
public class GitHubWebhookController {

    @PostMapping("/installation")
    public ResponseEntity<String> handleInstallation(@RequestBody String payload,
                                                   @RequestHeader("X-GitHub-Event") String event) {
        try {
            if ("installation".equals(event)) {
                // GitHub App 설치 완료 처리
                gitHubService.processInstallationEvent(payload);
            }
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("GitHub webhook 처리 중 오류 발생", e);
            return ResponseEntity.status(500).body("Error");
        }
    }
}
```

## 4. 보안 강화

### SecurityConfig 수정
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/github/**").permitAll() // GitHub 콜백은 인증 불필요
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/github/webhook/**") // Webhook은 CSRF 제외
            );
        
        return http.build();
    }
}
```

## 5. 프론트엔드 연동 코드 예시

```javascript
// 프론트엔드에서 GitHub 연동 시작
const connectGitHub = async (teamId) => {
  const returnUrl = `/teams/${teamId}/settings`; // 연동 완료 후 돌아갈 페이지
  const connectUrl = `${API_BASE_URL}/github/connect?teamId=${teamId}&returnUrl=${returnUrl}`;
  
  // 새 창에서 GitHub 연동 진행
  window.open(connectUrl, 'github-connect', 'width=600,height=700');
};

// 연동 완료 감지
window.addEventListener('message', (event) => {
  if (event.data.type === 'GITHUB_CONNECT_SUCCESS') {
    // 연동 성공 처리
    location.reload();
  }
});
```

## 6. 환경변수 설정

### Docker/K8s 환경변수
```bash
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
GITHUB_APP_ID=your_github_app_id
GITHUB_PRIVATE_KEY_PATH=/path/to/private-key.pem
SPRING_PROFILES_ACTIVE=prod
```

이러한 수정을 통해 배포 환경에서도 안전하고 원활한 GitHub 연동이 가능합니다.