package com.example.service.member;

import com.example.domain.member.MemberService;
import com.example.domain.member.Role;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.web.dto.MemberJoinDTO;
import com.example.web.exception.MemberValidateException;
import com.example.web.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Null;
import java.util.Optional;

import static com.example.web.exception.MemberValidateError.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private static final String JOIN_SUCCESS = "join success!";

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ApiResponse<Null> join(MemberJoinDTO memberJoinDTO) {
        Member member = convertToMember(memberJoinDTO);
        validateDuplicateMember(member);
        memberRepository.save(member);
        return new ApiResponse<>(HttpStatus.CREATED.value(), JOIN_SUCCESS);
    }

    private Member convertToMember(MemberJoinDTO memberJoinDTO) {
        encodePassword(memberJoinDTO);
        Member member = memberJoinDTO.convertToMember();
        return member;
    }

    private void encodePassword(MemberJoinDTO memberJoinDTO) {
        String encodedPassword = passwordEncoder.encode(memberJoinDTO.getPassword());
        memberJoinDTO.setPassword(encodedPassword);
    }

    private void validateDuplicateMember(Member member) {
        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(member.getEmail());
        if (memberByEmail.isPresent()) {
            throw new MemberValidateException(EXIST_MEMBER);
        }
    }

    @Override
    @Transactional
    public void adjustRole(Member member, Role role) {
        Member unAdjustedRoleMember = getMember(member, role);
        Member adjustedRoleMember = new Member(unAdjustedRoleMember, role);
        memberRepository.save(adjustedRoleMember);
    }

    private Member getMember(Member member, Role role) {
        Optional<Member> byId = memberRepository.findById(member.getId());
        if (byId.isEmpty()) {
            throw new MemberValidateException(NO_EXIST_MEMBER);
        }
        Member byIdMember = byId.get();
        checkAdmin(role, byIdMember);
        checkMember(role, byIdMember);
        return byIdMember;
    }

    private static void checkAdmin(Role role, Member byIdMember) {
        if (role.equals(Role.ADMIN)) {
            if (byIdMember.getRole()
                    .isAdmin()) {
                throw new MemberValidateException(ALREADY_ADMIN);
            }
        }
    }

    private static void checkMember(Role role, Member byIdMember) {
        if (role.equals(Role.MEMBER)) {
            if (byIdMember.getRole()
                    .isMember()) {
                throw new MemberValidateException(ALREADY_MEMBER);
            }
        }
    }
}
