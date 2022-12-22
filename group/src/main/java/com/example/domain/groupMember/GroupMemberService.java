package com.example.domain.groupMember;

import com.example.domain.group.Group;

public interface GroupMemberService {

    void participateGroup(Long memberId, Long groupId);

    void secessionGroup(Long memberId, Long groupId);

    void exileMember(Long ownerId, Long groupId, Long memberId);
}
