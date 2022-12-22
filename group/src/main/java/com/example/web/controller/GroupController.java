package com.example.web.controller;

import com.example.domain.group.GroupService;
import com.example.web.dto.GroupDTO;
import com.example.web.dto.GroupOwnerInfoDTO;
import com.example.web.response.ApiResponse;
import com.example.web.response.ApiResponseGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupController {

    private final GroupService service;

    @PostMapping("/admin")
    public ApiResponse<ApiResponse.withCodeAndMessage> makeGroup(GroupDTO groupDTO) {
        service.makeGroup(groupDTO);
        return ApiResponseGenerator.success(HttpStatus.OK, 1300, "success make group");
    }


    @DeleteMapping("/admin")
    public ApiResponse<ApiResponse.withCodeAndMessage> deleteGroup(GroupOwnerInfoDTO groupOwnerInfoDTO) {
        service.deleteGroup(groupOwnerInfoDTO.getGroupId(), groupOwnerInfoDTO.getOwnerId());
        return ApiResponseGenerator.success(HttpStatus.OK, 1300, "success delete group");
    }

    @PostMapping("/admin/name")
    public ApiResponse<ApiResponse.withCodeAndMessage> modifyGroupName(GroupOwnerInfoDTO groupOwnerInfoDTO, String groupName) {
        service.modifyGroupName(groupOwnerInfoDTO.getGroupId(), groupOwnerInfoDTO.getOwnerId(), groupName);
        return ApiResponseGenerator.success(HttpStatus.OK, 1300, "success modify groupName");
    }

    @PutMapping("/admin/max")
    public ApiResponse<ApiResponse.withCodeAndMessage> modifyGroupMaxMember(GroupOwnerInfoDTO groupOwnerInfoDTO, Integer maxMember) {
        service.modifyGroupMaxMember(groupOwnerInfoDTO.getGroupId(), groupOwnerInfoDTO.getOwnerId(), maxMember);
        return ApiResponseGenerator.success(HttpStatus.OK, 1300, "success modify group max Member");
    }

    @PutMapping("/admin/owner")
    public ApiResponse<ApiResponse.withCodeAndMessage> modifyGroupOwner(GroupOwnerInfoDTO groupOwnerInfoDTO, Long newOwnerId) {
        service.modifyGroupOwner(groupOwnerInfoDTO.getGroupId(), groupOwnerInfoDTO.getOwnerId(), newOwnerId);
        return ApiResponseGenerator.success(HttpStatus.OK, 1300, "success modify group owner");
    }

}
