package com.example.domain.token.accessToken;

import com.example.domain.member.MemberLogoutEvent;
import com.example.utils.token.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberLogoutEventHandler {

    private final AccessTokenRepository accessTokenRepository;

    @Async
    @EventListener
    @Transactional
    public void handle(MemberLogoutEvent event) {
        String accessTokenValue = event.getAccessTokenValue();
        String uuid = JwtToken.getUUID(accessTokenValue);
        accessTokenRepository.deleteById(uuid);
    }

}
