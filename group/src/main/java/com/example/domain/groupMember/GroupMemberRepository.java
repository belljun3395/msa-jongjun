package com.example.domain.groupMember;

import com.example.domain.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    Optional<GroupMember> findByMemberId(Long memberId);

    Optional<GroupMember> findByMemberIdAndGroupId(Long memberId, Long groupId);

    List<GroupMember> findAllByMemberId(Long memberId);

    List<GroupMember> findAllByGroup(Group group);
}
