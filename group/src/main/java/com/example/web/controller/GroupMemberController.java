package com.example.web.controller;

import com.example.domain.groupMember.GroupMemberService;
import com.example.web.dto.GroupDTO;
import com.example.web.dto.GroupMemberInfoDTO;
import com.example.web.dto.GroupOwnerInfoDTO;
import com.example.web.response.ApiResponse;
import com.example.web.response.ApiResponseGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups/members")
@RequiredArgsConstructor
public class GroupMemberController {

    private final GroupMemberService service;

    @GetMapping("/{memberId}")
    public ApiResponse<ApiResponse.withData> browseGroupByMember(@PathVariable("memberId") Long memberId) {
        List<GroupDTO> groups = service.browseGroup(memberId);
        return ApiResponseGenerator.success(groups, HttpStatus.OK, 1300, "member's group");
    }

    @PostMapping
    public ApiResponse<ApiResponse.withCodeAndMessage> participateGroup(GroupMemberInfoDTO groupMemberInfoDTO) {
        service.participateGroup(groupMemberInfoDTO.getMemberId(), groupMemberInfoDTO.getGroupId());
        return ApiResponseGenerator.success(HttpStatus.OK, 1300, "success participate group");
    }

    @DeleteMapping
    public ApiResponse<ApiResponse.withCodeAndMessage> secessionGroup(GroupMemberInfoDTO groupMemberInfoDTO) {
        service.secessionGroup(groupMemberInfoDTO.getMemberId(), groupMemberInfoDTO.getGroupId());
        return ApiResponseGenerator.success(HttpStatus.OK, 1300, "success secession group");
    }

    @DeleteMapping("/admin")
    public ApiResponse<ApiResponse.withCodeAndMessage> exileMember(GroupOwnerInfoDTO groupOwnerInfoDTO, Long memberId) {
        service.exileMember(groupOwnerInfoDTO.getOwnerId(), groupOwnerInfoDTO.getGroupId(), memberId);
        return ApiResponseGenerator.success(HttpStatus.OK, 1300, "success exile group member");
    }

}
