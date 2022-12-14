package com.example.service.member;

import com.example.domain.authMemberInfo.AuthMemberInfo;
import com.example.domain.authMemberInfo.AuthMemberInfoRepository;
import com.example.domain.member.MemberLoginInfo;
import com.example.domain.member.*;
import com.example.utils.token.TokenConfig;
import com.example.utils.token.TokenProvider;
import com.example.web.dto.*;
import com.example.web.exception.MemberValidateException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.*;

import static com.example.web.exception.MemberValidateError.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final TokenConfig tokenConfig;

    private final TokenProvider tokenProvider;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final KafkaTemplate<String, MemberAuthInfoDTO> memberAuthInfoDTOKafkaTemplate;

    private final AuthMemberInfoRepository authMemberInfoRepository;

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

    // todo ???????????? ????????? ?????? ??????
    @Override
    @Transactional
    public TokenDTO login(MemberLoginDTO memberLoginDTO) {
        String email = memberLoginDTO.getEmail();
        String password = memberLoginDTO.getPassword();
        Member member = findMemberBy(email);

        validatePassword(password, member);

        MemberLoginInfo memberLoginInfo = makeMemberLoginInfo(member, memberLoginDTO);
        applicationEventPublisher.publishEvent(new MemberLoginEvent(memberLoginInfo));

        String accessToken = getAccessToken(member.getId(), member.getRole());
        String refreshToken = getRefreshToken(member.getId(), member.getRole());
        return new TokenDTO(accessToken, refreshToken);
    }

    private MemberLoginInfo makeMemberLoginInfo(Member member, MemberLoginDTO memberLoginDTO) {
        return MemberLoginInfo.builder()
                .memberId(member.getId())
                .role(member.getRole())
                .clientType(memberLoginDTO.getClientType())
                .location(memberLoginDTO.getLocation())
                .build();
    }

    private String getAccessToken(Long memberId, Role role) {
        Map<String, Object> memberClaims = getMemberClaims(memberId, role);

        long now = System.currentTimeMillis();
        return tokenProvider.getToken(now + tokenConfig.getTwentyMin(), memberClaims);
    }

    private String getRefreshToken(Long memberId, Role role) {
        Map<String, Object> memberClaims = getMemberClaims(memberId, role);

        long now = System.currentTimeMillis();
        return tokenProvider.getToken(now + tokenConfig.getOneDay(), memberClaims);
    }

    private Map<String, Object> getMemberClaims(Long memberId, Role role) {
        Map<String, Object> memberClaims = new HashMap<>();
        memberClaims.put(tokenConfig.getMemberIdKey(), memberId);
        memberClaims.put(tokenConfig.getRoleKey(), role);
        return memberClaims;
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

    @Override
    public String emailAuth(MemberAuthInfoDTO memberAuthInfoDTO) {

        String uuid = createKey();
        String key = createKey();
        memberAuthInfoDTO.setUuid(uuid);
        memberAuthInfoDTO.setKey(key);
        ListenableFuture<SendResult<String, MemberAuthInfoDTO>> emailAuth = memberAuthInfoDTOKafkaTemplate.send("emailAuth", memberAuthInfoDTO);

        emailAuth.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, MemberAuthInfoDTO> result) {
                System.out.println("Sent message=[" + memberAuthInfoDTO +
                        "] with offset=[" + result.getRecordMetadata()
                        .offset() + "]");
                authMemberInfoRepository.save(new AuthMemberInfo(uuid, key));
            }

            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Unable to send message=["
                        + memberAuthInfoDTO + "] due to : " + ex.getMessage());
            }
        });

        return uuid;
    }

    private String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();
        for (int i = 0; i < 8; i++) { // ???????????? 8??????
            int index = rnd.nextInt(3); // 0~2 ?????? ??????
            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    break;
            }
        }
        return key.toString();
    }

    @Override
    public boolean validateAuthKey(AuthKeyInfoDTO authKeyInfoDTO) {
        Optional<AuthMemberInfo> authMemberInfoById = authMemberInfoRepository.findById(authKeyInfoDTO.getUuid());
        if (authMemberInfoById.isEmpty()) {
            throw new IllegalStateException("no email auth record");
        }
        AuthMemberInfo authMemberInfo = authMemberInfoById.get();
        return authMemberInfo.getKey()
                .equals(authKeyInfoDTO.getAuthKey());
    }

    @Override
    public MemberInfoDTO memberInfo(Long memberId) {
        Optional<Member> memberById = repository.findById(memberId);
        if (memberById.isEmpty()) {
            throw new MemberValidateException(NO_EXIST_MEMBER);
        }
        Member member = memberById.get();
        return new MemberInfoDTO(member.getId(), member.getEmail(), member.getName(), member.getRole());
    }
}
