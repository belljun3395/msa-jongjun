package com.example.domain.group;

import com.example.web.dto.GroupDTO;

import java.util.List;

public interface GroupService {

    List<GroupDTO> browseGroups();

    List<GroupDTO> browseOwnerGroups(Long ownerId);

    void makeGroup(GroupDTO group);

    void deleteGroup(Long groupId, Long ownerId);

    void modifyGroupName(Long groupId, Long ownerId, String groupName);

    void modifyGroupMaxMember(Long groupId, Long ownerId, Integer maxMember);

    void modifyGroupOwner(Long groupId, Long ownerId, Long newOwnerId);
}
