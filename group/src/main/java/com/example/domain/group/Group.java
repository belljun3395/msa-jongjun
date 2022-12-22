package com.example.domain.group;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "group_table")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    private String groupName;

    private Integer maxMember;

    private Long ownerId;

    public Group(String groupName, Integer maxMember, Long ownerId) {
        this.groupName = groupName;
        this.maxMember = maxMember;
        this.ownerId = ownerId;
    }

    public void modifyGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void modifyGroupMaxMember(Integer maxMember) {
        this.maxMember = maxMember;
    }

    public void modifyGroupOwner(Long ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isOwner(Long id) {
        return ownerId == id;
    }

    public boolean isMax(Integer memberCount) {
        return memberCount == maxMember;
    }
}
