package com.example.domain.member;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MemberLoginEvent extends ApplicationEvent {

    private MemberLoginInfo memberLoginInfo;

    public MemberLoginEvent(MemberLoginInfo memberLoginInfo) {
        super(memberLoginInfo);
        this.memberLoginInfo = memberLoginInfo;
    }

}
