package com.example.web.dto;

import com.example.domain.group.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GroupDTO {

    private Long groupId;

    private String groupName;

    private Integer maxMember;

    private Long ownerId;

    public Group convertToGroup() {
        return new Group(groupName, maxMember, ownerId);
    }
}
