package com.example.service.group;

import com.example.domain.group.Group;
import com.example.domain.group.GroupRepository;
import com.example.domain.group.GroupService;
import com.example.web.dto.GroupDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository repository;

    @Override
    @Transactional
    public void makeGroup(GroupDTO group) {
        repository.save(group.convertToGroup());
    }

    @Override
    @Transactional
    public void deleteGroup(Long groupId, Long ownerId) {
        Optional<Group> groupById = repository.findById(groupId);
        if (groupById.isEmpty()) {
            throw new IllegalStateException("no group");
        }
        Group group = groupById.get();
        if (group.getOwnerId() != ownerId) {
            throw new IllegalStateException("no role for you");
        }
        repository.deleteById(groupId);
    }

    @Override
    @Transactional
    public void modifyGroupName(Long groupId, Long ownerId, String groupName) {
        Optional<Group> groupById = repository.findById(groupId);
        if (groupById.isEmpty()) {
            throw new IllegalStateException("no group");
        }
        Group group = groupById.get();
        if (group.getOwnerId() != ownerId) {
            throw new IllegalStateException("no role for you");
        }
        group.modifyGroupName(groupName);
    }

    @Override
    @Transactional
    public void modifyGroupMaxMember(Long groupId, Long ownerId, Integer maxMember) {
        Optional<Group> groupById = repository.findById(groupId);
        if (groupById.isEmpty()) {
            throw new IllegalStateException("no group");
        }
        Group group = groupById.get();
        if (group.getOwnerId() != ownerId) {
            throw new IllegalStateException("no role for you");
        }
        group.modifyGroupMaxMember(maxMember);
    }

    @Override
    @Transactional
    public void modifyGroupOwner(Long groupId, Long ownerId, Long newOwnerId) {
        Optional<Group> groupById = repository.findById(groupId);
        if (groupById.isEmpty()) {
            throw new IllegalStateException("no group");
        }
        Group group = groupById.get();
        if (!group.isOwner(ownerId)) {
            throw new IllegalStateException("not owner");
        }
        group.modifyGroupOwner(newOwnerId);
    }
}
