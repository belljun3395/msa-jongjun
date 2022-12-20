package com.example.domain.member;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MemberLogoutEvent extends ApplicationEvent {

    private final String accessTokenValue;

    public MemberLogoutEvent(String accessTokenValue) {
        super(accessTokenValue);
        this.accessTokenValue = accessTokenValue;
    }

}
