package com.example.service.member;

import com.example.domain.member.MemberLoginInfo;
import com.example.domain.member.*;
import com.example.web.dto.MemberJoinDTO;
import com.example.web.dto.MemberLoginDTO;
import com.example.web.dto.TokenDTO;
import com.example.web.exception.MemberValidateException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.web.exception.MemberValidateError.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public void join(MemberJoinDTO memberJoinDTO) {
        Member member = convertToMember(memberJoinDTO);
        validateDuplicateMember(member);
        repository.save(member);
    }

    private Member convertToMember(MemberJoinDTO memberJoinDTO) {
        encodePassword(memberJoinDTO);
        return memberJoinDTO.convertToMember();
    }

    private void encodePassword(MemberJoinDTO memberJoinDTO) {
        String encodedPassword = passwordEncoder.encode(memberJoinDTO.getPassword());
        memberJoinDTO.setPassword(encodedPassword);
    }

    private void validateDuplicateMember(Member member) {
        Optional<Member> memberByEmail = repository.findMemberByEmail(member.getEmail());
        if (memberByEmail.isPresent()) {
            throw new MemberValidateException(EXIST_MEMBER);
        }
    }

    // todo 트랜잭션 원자성 문제 확인
    @Override
    @Transactional
    public TokenDTO login(MemberLoginDTO memberLoginDTO) {
        String email = memberLoginDTO.getEmail();
        String password = memberLoginDTO.getPassword();
        Member member = findMemberBy(email);

        validatePassword(password, member);

        MemberLoginInfo memberLoginInfo = makeMemberLoginInfo(member, memberLoginDTO);
        applicationEventPublisher.publishEvent(new MemberLoginEvent(memberLoginInfo));

        return new TokenDTO(memberLoginInfo.getAccessToken(), memberLoginInfo.getRefreshToken());
    }

    private static MemberLoginInfo makeMemberLoginInfo(Member member, MemberLoginDTO memberLoginDTO) {
        return MemberLoginInfo.builder()
                .memberId(member.getId())
                .role(member.getRole())
                .clientType(memberLoginDTO.getClientType())
                .location(memberLoginDTO.getLocation())
                .build();
    }

    private Member findMemberBy(String email) {
        Optional<Member> memberByEmail = repository.findMemberByEmail(email);
        if (memberByEmail.isEmpty()) {
            throw new MemberValidateException(NO_EXIST_MEMBER);
        }
        return memberByEmail.get();
    }

    private void validatePassword(String password, Member member) {
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new MemberValidateException(LOGIN_FAIL);
        }
    }

    @Override
    @Transactional
    public void adjustRole(Long memberId, Role role) {
        Member unAdjustedRoleMember = getMember(memberId, role);
        repository.save(new Member(unAdjustedRoleMember, role));
    }

    private Member getMember(Long memberId, Role role) {
        Optional<Member> memberById = repository.findById(memberId);
        if (memberById.isEmpty()) {
            throw new MemberValidateException(NO_EXIST_MEMBER);
        }

        isAdmin(role, memberById.get());
        isMember(role, memberById.get());
        return memberById.get();
    }

    private static void isAdmin(Role role, Member memberById) {
        if (role.equals(Role.ADMIN)) {
            if (memberById.getRole()
                    .isAdmin()) {
                throw new MemberValidateException(ALREADY_ADMIN);
            }
        }
    }

    private static void isMember(Role role, Member memberById) {
        if (role.equals(Role.MEMBER)) {
            if (memberById.getRole()
                    .isMember()) {
                throw new MemberValidateException(ALREADY_MEMBER);
            }
        }
    }

    @Override
    public void logout(String accessTokenValue) {
        applicationEventPublisher.publishEvent(new MemberLogoutEvent(accessTokenValue));
    }
}
