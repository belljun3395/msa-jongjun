package com.example.service.member;

import com.example.domain.member.Role;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private static final String ERROR = "[ERROR] ";
    private static final String ERROR_EXIST_MEMBER = ERROR + "there are already our member";
    private static final String ERROR_NO_EXIST_MEMBER = ERROR + "there are no exist member information";
    private static final String ERROR_ALREADY_ADMIN = ERROR + "this member is already admin";
    private static final String ERROR_ALREADY_MEMBER = ERROR + "this member is already member";


    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void join(Member member) {
        vaildateDuplicateMember(member);
        memberRepository.save(member);
    }

    private void vaildateDuplicateMember(Member member) {
        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(member.getEmail());
        if (memberByEmail.isPresent()) {
            throw new IllegalStateException(ERROR_EXIST_MEMBER);
        }
    }

    @Override
    @Transactional
    public void adjustRole(Member member, Role role) {
        Member memberVisitor = getMember(member, role);
        Member adminMember = new Member(memberVisitor, role);
        memberRepository.save(adminMember);
    }

    private Member getMember(Member member, Role role) {
        Optional<Member> byId = memberRepository.findById(member.getId());
        if (byId.isEmpty()) {
            throw new IllegalStateException(ERROR_NO_EXIST_MEMBER);
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
                throw new IllegalStateException(ERROR_ALREADY_ADMIN);
            }
        }
    }

    private static void checkMember(Role role, Member byIdMember) {
        if (role.equals(Role.MEMBER)) {
            if (byIdMember.getRole()
                    .isMember()) {
                throw new IllegalStateException(ERROR_ALREADY_MEMBER);
            }
        }
    }
}
