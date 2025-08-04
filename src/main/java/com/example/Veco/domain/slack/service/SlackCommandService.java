package com.example.Veco.domain.slack.service;

import com.example.Veco.domain.external.converter.ExternalServiceConverter;
import com.example.Veco.domain.external.entity.ExternalService;
import com.example.Veco.domain.external.repository.ExternalServiceRepository;
import com.example.Veco.domain.mapping.converter.LinkConverter;
import com.example.Veco.domain.mapping.entity.Link;
import com.example.Veco.domain.mapping.repository.LinkRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.error.MemberErrorStatus;
import com.example.Veco.domain.member.error.MemberHandler;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.slack.converter.SlackConverter;
import com.example.Veco.domain.slack.dto.SlackResDTO;
import com.example.Veco.domain.slack.exception.SlackException;
import com.example.Veco.domain.slack.exception.code.SlackErrorCode;
import com.example.Veco.domain.slack.util.SlackUtil;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import com.example.Veco.global.auth.jwt.util.JwtUtil;
import com.example.Veco.global.enums.ExtServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackCommandService {

    // 리포지토리
    private final ExternalServiceRepository externalServiceRepository;
    private final LinkRepository linkRepository;
    private final MemberRepository memberRepository;

    // 유틸
    private final JwtUtil jwtUtil;
    private final SlackUtil slackUtil;

    // yml 값
    @Value("${slack.client-id}")
    private String clientId;
    @Value("${slack.scope}")
    private String scope;

    // 리다이렉트 링크 생성
    public RedirectView redirectSlackOAuth(
            String token
    ){
        // 토큰 Bearer 제거
        token = token.replace("Bearer ", "");

        String url = "https://slack.com/oauth/v2/authorize?" +
                "client_id="+clientId+
                "&scope="+scope+
                "&state="+token;

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(url);
        return redirectView;
    }

    // Slack 연동 비즈니스 로직
    @Transactional
    public SlackResDTO.InstallApp installApp(
            String code,
            String state
    ){

        // JWT 토큰 -> Member
        Member member = memberRepository.findBySocialUid(jwtUtil.getUsername(state))
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND));

        // Bot Access Token 발급
        SlackResDTO.ExchangeAccessToken tokenResult = slackUtil.ExchangeAccessToken(code);

        // 정상적으로 토큰 발급되었는지 검증
        if (!(tokenResult != null && tokenResult.ok())){
            log.error("[Slack 토큰 발급 실패]: {}", Objects.requireNonNull(tokenResult).error());
            throw new SlackException(SlackErrorCode.TOKEN_FAILED);
        }

        // 채널 리스트 조회: general 채널이 나올때까지 조회
        String channelId = "";
        boolean breakPoint = false;
        do {
            SlackResDTO.GetChannelList channelResult = slackUtil.GetChannelList(tokenResult.access_token());

            // 응답이 제대로 오지 않은 경우
            if (!(channelResult != null && channelResult.ok())) {
                throw new SlackException(SlackErrorCode.LIST_FAILED);
            }

            // general 채널이 존재하는지 확인
            for (SlackResDTO.Channel value : channelResult.channels()) {
                if (value.is_general()) {
                    channelId = value.id();
                    breakPoint = true;
                    break;
                }
            }

        } while (!breakPoint);

        // 만약 general 채널이 없는 경우 (가능성 0%)
        if (channelId.isEmpty()) {
            throw new SlackException(SlackErrorCode.LIST_FAILED);
        }

        // 채널 참여
        SlackResDTO.JoinChannel joinResult = slackUtil.joinChannel(tokenResult.access_token(), channelId);

        // 창여 실패시
        if (!(joinResult != null && joinResult.ok())) {
            throw new SlackException(SlackErrorCode.JOIN_FAILED);
        }

        // 조회한 정보들 모두 저장 (연동)
        WorkSpace workspace = member.getWorkSpace();

        // 이미 존재하면 update
        Optional<Link> link = linkRepository.findLinkByWorkspaceAndExternalService_ServiceType(
                workspace, ExtServiceType.SLACK
        );
        if (link.isPresent()) {
            LocalDateTime now = LocalDateTime.now();
            link.get().getExternalService().updateSlackDefaultChannelId(channelId);
            link.get().getExternalService().updateAccessToken(tokenResult.access_token());
            link.get().updateLinkedAt(now);

            return SlackConverter.toInstallApp(
                    link.get().getWorkspace().getId(), link.get().getLinkedAt()
            );
        } else { // 존재하지 않는 경우

            // 객체 생성
            ExternalService externalService = ExternalServiceConverter.toExternalService(
                    ExtServiceType.SLACK,
                    tokenResult.access_token(),
                    channelId
            );
            LocalDateTime now = LocalDateTime.now();
            Link newLink = LinkConverter.toLink(now, workspace, externalService);

            externalServiceRepository.save(externalService);
            Link result = linkRepository.save(newLink);

            return SlackConverter.toInstallApp(result.getWorkspace().getId(), result.getLinkedAt());
        }
    }
}
