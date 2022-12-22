package com.example.service.groupMember;

import com.example.domain.group.Group;
import com.example.domain.group.GroupRepository;
import com.example.domain.groupMember.GroupMember;
import com.example.domain.groupMember.GroupMemberRepository;
import com.example.domain.groupMember.GroupMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupMemberServiceImpl implements GroupMemberService {

    private final GroupMemberRepository repository;
    private final GroupRepository groupRepository;

    @Override
    @Transactional
    public void participateGroup(Long memberId, Long groupId) {
        Optional<Group> groupById = groupRepository.findById(groupId);
        if (groupById.isEmpty()) {
            throw new IllegalStateException("no such group");
        }
        Group group = groupById.get();
        List<GroupMember> allMembersByGroup = repository.findAllByGroup(group);
        if (group.isMax(allMembersByGroup.size())) {
            throw new IllegalStateException("can't join this group more");
        }
        repository.save(new GroupMember(memberId, group));
    }

    @Override
    @Transactional
    public void secessionGroup(Long memberId, Long groupId) {
        Optional<Group> groupById = groupRepository.findById(groupId);
        if (groupById.isEmpty()) {
            throw new IllegalStateException("no such group");
        }
        Group group = groupById.get();
        Optional<GroupMember> groupMemberByMemberId = repository.findByMemberId(memberId);
        if (groupMemberByMemberId.isEmpty()) {
            throw new IllegalStateException("no member");
        }
        GroupMember groupMember = groupMemberByMemberId.get();
        if (!groupMember.getGroup()
                .equals(group)) {
            throw new IllegalStateException("not match group");
        }
        repository.delete(groupMember);
    }

    @Override
    @Transactional
    public void exileMember(Long ownerId, Long groupId, Long memberId) {
        Optional<Group> groupById = groupRepository.findById(groupId);
        if (groupById.isEmpty()) {
            throw new IllegalStateException("no such group");
        }
        Group group = groupById.get();
        Optional<GroupMember> groupMemberByMemberId = repository.findByMemberId(memberId);
        if (groupMemberByMemberId.isEmpty()) {
            throw new IllegalStateException("no member");
        }
        GroupMember groupMember = groupMemberByMemberId.get();
        if (!groupMember.getGroup()
                .equals(group)) {
            throw new IllegalStateException("not match group");
        }
        if (!groupMember.getGroup()
                .isOwner(ownerId)) {
            throw new IllegalStateException("not allowed role");
        }
        repository.delete(groupMember);
    }
}
