package com.example.service.token.accessToken;

import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.token.accessToken.AccessToken;
import com.example.domain.token.accessToken.AccessTokenRepository;
import com.example.domain.token.accessToken.AccessTokenService;
import com.example.utils.token.JwtToken;
import com.example.utils.token.TokenConfig;
import com.example.web.dto.MemberInfoDTO;
import com.example.web.exception.MemberValidateError;
import com.example.web.exception.MemberValidateException;
import com.example.web.exception.TokenValidateError;
import com.example.web.exception.TokenValidateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.example.domain.member.Role.makeRole;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccessTokenServiceImpl implements AccessTokenService {

    private final AccessTokenRepository repository;

    private final MemberRepository memberRepository;


    @Override
    @Transactional
    public void save(AccessToken accessToken) {
        repository.save(accessToken);
    }

    @Override
    @Transactional
    public MemberInfoDTO browseMemberMatch(String accessTokenValue) {
        AccessToken accessToken = getAccessTokenBy(JwtToken.getUUID(accessTokenValue));
        Member member = getMemberBy(accessToken);
        return MemberInfoDTO.convertFrom(member);
    }

    @Override
    public AccessToken findAccessToken(String accessTokenValue) {
        return getAccessTokenBy(JwtToken.getUUID(accessTokenValue));
    }

    @Override
    public AccessToken makeAccessToken(String refreshToken) {
        JwtToken.refreshExpirationTime(refreshToken);
        Long memberId = Long.valueOf(JwtToken.decodeToken(refreshToken, TokenConfig.MEMBERID_KEY));
        String Role = JwtToken.decodeToken(refreshToken, TokenConfig.ROLE_KEY);
        AccessToken accessToken =
                new AccessToken(JwtToken.makeToken(System.currentTimeMillis() + TokenConfig.TWENTY_MIN, this.makeUUID()),
                                                    memberId,
                                                    makeRole(Role));
        repository.save(accessToken);
        return accessToken;
    }

    private Map<String, Object> makeUUID() {
        HashMap<String, Object> uuidInfo = new HashMap<>();
        uuidInfo.put(TokenConfig.UUID_KEY, UUID.randomUUID());
        return uuidInfo;
    }

    private AccessToken getAccessTokenBy(String accessTokenValue) {
        Optional<AccessToken> tokenById = repository.findById(accessTokenValue);
        if (tokenById.isEmpty()) {
            throw new TokenValidateException(TokenValidateError.NO_TOKEN_LOG);
        }
        return tokenById.get();
    }

    private Member getMemberBy(AccessToken accessToken) {
        Optional<Member> memberById = memberRepository.findById(accessToken.getMemberId());
        if (memberById.isEmpty()) {
            throw new MemberValidateException(MemberValidateError.NO_EXIST_MEMBER);
        }
        return memberById.get();
    }

    @Override
    public boolean validateAccessTokenRole(String accessTokenValue, String role) {
        Optional<AccessToken> tokenById = repository.findById(JwtToken.getUUID(accessTokenValue));
        if (tokenById.isEmpty()) {
            throw new TokenValidateException(TokenValidateError.NO_TOKEN_LOG);
        }
        AccessToken accessToken = tokenById.get();
        return role.equals(accessToken.getRole()
                .getType());
    }
}
