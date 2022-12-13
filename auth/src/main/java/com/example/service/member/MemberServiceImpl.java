package com.example.service.member;

import com.example.domain.member.MemberLoginInfo;
import com.example.domain.member.*;
import com.example.web.dto.MemberJoinDTO;
import com.example.web.dto.MemberLoginDTO;
import com.example.web.dto.TokenDTO;
import com.example.web.exception.MemberValidateException;
import com.example.web.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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

    private final ApplicationEventPublisher applicationEventPublisher;

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

    // todo 트랜잭션 원자성 문제 확인
    @Override
    @Transactional
    public TokenDTO login(MemberLoginDTO memberLoginDTO) {
        String email = memberLoginDTO.getEmail();
        String password = memberLoginDTO.getPassword();
        String clientType = memberLoginDTO.getClientType();
        String location = memberLoginDTO.getLocation();
        Member member = findMemberBy(email);
        validatePassword(password, member);
        MemberLoginInfo memberLoginInfo = new MemberLoginInfo(member.getId(), member.getRole(), clientType, location);
        applicationEventPublisher.publishEvent(new MemberLoginEvent(memberLoginInfo));
        return new TokenDTO(memberLoginInfo.getAccessToken(), memberLoginInfo.getRefreshToken());
    }

    private Member findMemberBy(String email) {
        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(email);
        if (memberByEmail.isEmpty()) {
            throw new MemberValidateException(NO_EXIST_MEMBER);
        }
        return memberByEmail.get();
    }

    // todo Login Exception 구현
    private void validatePassword(String password, Member member) {
        boolean matches = passwordEncoder.matches(password, member.getPassword());
        if (!matches) {
            throw new IllegalStateException("로그인 실패");
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
