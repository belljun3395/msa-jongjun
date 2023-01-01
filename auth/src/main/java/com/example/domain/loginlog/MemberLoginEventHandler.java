package com.example.domain.loginlog;

import com.example.domain.member.MemberLoginEvent;
import com.example.domain.member.MemberLoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class MemberLoginEventHandler {

    private final LoginLogRepository repository;

    @Async
    @EventListener
    @Transactional
    public void handle(MemberLoginEvent event) {
        MemberLoginInfo memberLoginInfo = event.getMemberLoginInfo();
        // todo 동시 로그인 문제 해결
        repository.save(loginLogFrom(memberLoginInfo));
    }

    private LoginLog loginLogFrom(MemberLoginInfo memberLoginInfo) {
        return LoginLog.builder()
                .memberId(memberLoginInfo.getMemberId())
                .role(memberLoginInfo.getRole())
                .clientType(memberLoginInfo.getClientType())
                .location(memberLoginInfo.getLocation())
                .build();
    }
}
