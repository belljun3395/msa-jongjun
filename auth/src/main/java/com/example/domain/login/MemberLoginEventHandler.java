package com.example.domain.login;

import com.example.domain.member.MemberLoginEvent;
import com.example.domain.member.MemberLoginInfo;
import com.example.domain.token.accessToken.AccessToken;
import com.example.domain.token.accessToken.AccessTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class MemberLoginEventHandler {

    private final LoginLogRepository repository;
    private final AccessTokenRepository accessTokenRepository;

    @Async
    @EventListener
    @Transactional
    public void handle(MemberLoginEvent event) {
        MemberLoginInfo memberLoginInfo = event.getMemberLoginInfo();
        // todo 동시 로그인 문제 해결
        repository.save(loginLogFrom(memberLoginInfo));
        accessTokenRepository.save(accessTokenFrom(memberLoginInfo));
    }

    private AccessToken accessTokenFrom(MemberLoginInfo memberLoginInfo) {
        return new AccessToken(memberLoginInfo.getAccessToken(), memberLoginInfo.getMemberId(), memberLoginInfo.getRole());
    }

    private LoginLog loginLogFrom(MemberLoginInfo memberLoginInfo) {
        return LoginLog.builder()
                .memberId(memberLoginInfo.getMemberId())
                .role(memberLoginInfo.getRole())
                .clientType(memberLoginInfo.getClientType())
                .location(memberLoginInfo.getLocation())
                .refreshToken(memberLoginInfo.getRefreshToken())
                .build();
    }
}
