package com.example.service.group;

import com.example.domain.group.Group;
import com.example.domain.group.GroupRepository;
import com.example.domain.group.GroupService;
import com.example.web.dto.GroupDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository repository;

    @Override
    public List<GroupDTO> browseGroups() {
        List<Group> allGroup = repository.findAll();
        List<GroupDTO> groupDTOS = new ArrayList<>();
        for (Group group : allGroup) {
            groupDTOS.add(new GroupDTO(group.getId(), group.getGroupName(), group.getMaxMember(), group.getOwnerId()));
        }
        return groupDTOS;
    }

    @Override
    public List<GroupDTO> browseOwnerGroups(Long ownerId) {
        List<Group> allGroupByOwnerId = repository.findAllByOwnerId(ownerId);
        List<GroupDTO> groupDTOS = new ArrayList<>();
        for (Group group : allGroupByOwnerId) {
            groupDTOS.add(new GroupDTO(group.getId(), group.getGroupName(), group.getMaxMember(), group.getOwnerId()));
        }
        return groupDTOS;
    }

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
        if (!group.isOwner(ownerId)) {
            throw new IllegalStateException("you are not owner");
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
        if (!group.isOwner(ownerId)) {
            throw new IllegalStateException("you are not owner");
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
        if (!group.isOwner(ownerId)) {
            throw new IllegalStateException("you are not owner");
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
