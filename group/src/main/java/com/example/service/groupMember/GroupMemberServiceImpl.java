package com.example.service.groupMember;

import com.example.domain.group.Group;
import com.example.domain.group.GroupRepository;
import com.example.domain.groupMember.GroupMember;
import com.example.domain.groupMember.GroupMemberRepository;
import com.example.domain.groupMember.GroupMemberService;
import com.example.web.dto.GroupDTO;
import com.example.web.exception.GroupMemberValidateError;
import com.example.web.exception.GroupMemberValidateException;
import com.example.web.exception.GroupValidateError;
import com.example.web.exception.GroupValidateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupMemberServiceImpl implements GroupMemberService {

    private final GroupMemberRepository repository;
    private final GroupRepository groupRepository;

    @Override
    public List<GroupDTO> browseGroup(Long memberId) {
        List<GroupMember> groupMembers = repository.findAllByMemberId(memberId);
        List<GroupDTO> groups = new ArrayList<>();
        for (GroupMember gm : groupMembers) {
            // todo lazyLoading 확인
            Group group = gm.getGroup();
            groups.add(new GroupDTO(group.getId(), group.getGroupName(), group.getMaxMember(), group.getOwnerId()));
        }
        return groups;
    }

    @Override
    @Transactional
    public void participateGroup(Long memberId, Long groupId) {
        Optional<Group> groupById = groupRepository.findById(groupId);
        if (groupById.isEmpty()) {
            throw new GroupValidateException(GroupValidateError.NO_SUCH_GROUP);
        }
        Group group = groupById.get();
        Optional<GroupMember> memberByMemberId = repository.findByMemberId(memberId);
        if (memberByMemberId.isPresent()) {
            if (memberByMemberId.get()
                    .getGroup()
                    .getId()
                    .equals(groupId)) {
                throw new GroupMemberValidateException(GroupMemberValidateError.ALREADY_PARTICIPATED);
            }
        }
        List<GroupMember> allMembersByGroup = repository.findAllByGroup(group);
        if (group.isMax(allMembersByGroup.size())) {
            throw new GroupMemberValidateException(GroupMemberValidateError.NO_MORE_MEMBER);
        }
        repository.save(new GroupMember(memberId, group));
    }

    @Override
    @Transactional
    public void secessionGroup(Long memberId, Long groupId) {
        Optional<Group> groupById = groupRepository.findById(groupId);
        if (groupById.isEmpty()) {
            throw new GroupValidateException(GroupValidateError.NO_SUCH_GROUP);
        }
        Group group = groupById.get();
        Optional<GroupMember> groupMemberByMemberId = repository.findByMemberIdAndGroupId(memberId, groupId);
        if (groupMemberByMemberId.isEmpty()) {
            throw new GroupMemberValidateException(GroupMemberValidateError.NO_MEMBER);
        }
        GroupMember groupMember = groupMemberByMemberId.get();
        if (!groupMember.getGroup()
                .equals(group)) {
            throw new GroupMemberValidateException(GroupMemberValidateError.NOT_MATCH_GROUP);
        }
        repository.delete(groupMember);
    }

    @Override
    @Transactional
    public void exileMember(Long ownerId, Long groupId, Long memberId) {
        Optional<Group> groupById = groupRepository.findById(groupId);
        if (groupById.isEmpty()) {
            throw new GroupValidateException(GroupValidateError.NO_SUCH_GROUP);
        }
        Group group = groupById.get();
        Optional<GroupMember> groupMemberByMemberId = repository.findByMemberId(memberId);
        if (groupMemberByMemberId.isEmpty()) {
            throw new GroupMemberValidateException(GroupMemberValidateError.NO_MEMBER);
        }
        GroupMember groupMember = groupMemberByMemberId.get();
        if (!groupMember.getGroup()
                .equals(group)) {
            throw new GroupMemberValidateException(GroupMemberValidateError.NOT_MATCH_GROUP);
        }
        if (!groupMember.getGroup()
                .isOwner(ownerId)) {
            throw new GroupValidateException(GroupValidateError.NOT_OWNER);
        }
        repository.delete(groupMember);
    }
}
