package com.example.Veco.domain.external.controller;

import com.example.Veco.domain.external.dto.GitHubWebhookPayload;
import com.example.Veco.domain.external.service.GitHubIssueService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
@Slf4j
public class GitHubWebhookController {
    private final GitHubIssueService gitHubIssueService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody GitHubWebhookPayload payload,
            @RequestHeader("X-GitHub-Event") String eventType,
            @RequestHeader("X-GitHub-Delivery") String deliveryId,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            HttpServletRequest request) {

        log.info("Received GitHub webhook. Event: {}, Delivery: {}", eventType, deliveryId);

        try {
            // 서명 검증
//            if (!webhookSecurityService.verifySignature(request, signature)) {
//                log.warn("Invalid webhook signature for delivery: {}", deliveryId);
//                return ResponseEntity.status(401).body("Unauthorized");
//            }

            // Issues 이벤트만 처리
            if (!"issues".equals(eventType)) {
                log.debug("Ignoring non-issues event: {}", eventType);
                return ResponseEntity.ok("Event ignored");
            }

            // 이슈 처리
            gitHubIssueService.processIssueWebhook(payload);

            log.info("Successfully processed {} action for issue #{} in {}",
                    payload.getAction(),
                    payload.getIssue().getNumber(),
                    payload.getRepository().getFullName());

            return ResponseEntity.ok("Webhook processed successfully");

        } catch (Exception e) {
            log.error("Error processing webhook for delivery: {}", deliveryId, e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
}
