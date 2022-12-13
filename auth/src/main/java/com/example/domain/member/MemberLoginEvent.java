package com.example.domain.member;

import org.springframework.context.ApplicationEvent;

public class MemberLoginEvent extends ApplicationEvent {

    private MemberLoginInfo memberLoginInfo;

    public MemberLoginEvent(MemberLoginInfo memberLoginInfo) {
        super(memberLoginInfo);
        this.memberLoginInfo = memberLoginInfo;
    }

    public MemberLoginInfo getMemberLoginInfo() {
        return memberLoginInfo;
    }
}
