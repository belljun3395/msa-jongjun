package com.example.domain.groupMember;

import com.example.web.dto.GroupDTO;

import java.util.List;

public interface GroupMemberService {

    List<GroupDTO> browseGroup(Long memberId);

    void participateGroup(Long memberId, Long groupId);

    void secessionGroup(Long memberId, Long groupId);

    void exileMember(Long ownerId, Long groupId, Long memberId);
}
