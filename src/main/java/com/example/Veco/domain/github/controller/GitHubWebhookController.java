package com.example.Veco.domain.github.controller;

import com.example.Veco.domain.github.service.GitHubIssueService;
import com.example.Veco.domain.github.service.GitHubPullRequestService;
import io.swagger.v3.oas.annotations.Hidden;
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
    private final GitHubPullRequestService gitHubPullRequestService;

    @Hidden
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-GitHub-Event") String eventType,
            @RequestHeader("X-GitHub-Delivery") String deliveryId) {

        log.info("Received GitHub webhook. Event: {}, Delivery: {}", eventType, deliveryId);

        try {

            switch (eventType) {
                case "issues":
                    gitHubIssueService.processIssueWebhook(payload);
                    break;
                case "pull_request":

                    log.info(payload);

                    gitHubPullRequestService.handlePullRequestEvent(payload);
                    break;
                case "issue_comment":
                    break;
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Error processing webhook for delivery: {}", deliveryId, e);
            return ResponseEntity.status(500).body("Internal server error");
        }

    }
}
