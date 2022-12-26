package com.example.service.token.accessToken;

import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.member.Role;
import com.example.domain.token.accessToken.AccessToken;
import com.example.domain.token.accessToken.AccessTokenRepository;
import com.example.domain.token.accessToken.AccessTokenService;
import com.example.utils.token.JwtToken;
import com.example.web.dto.MemberInfoDTO;
import com.example.web.exception.MemberValidateError;
import com.example.web.exception.MemberValidateException;
import com.example.web.exception.TokenValidateError;
import com.example.web.exception.TokenValidateException;
import io.jsonwebtoken.JwtException;
import io.lettuce.core.RedisException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.example.domain.member.Role.*;
import static com.example.domain.member.Role.makeRole;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccessTokenServiceImpl implements AccessTokenService {

    private final String MEMBER_ID = "memberId";
    private final String ROLE_KEY = "role";
    private final Long NOW = System.currentTimeMillis();
    private final Long TWENTY_MIN = 20 * 60L * 1000L;

    private final Long ACCESS_TOKEN_EXP = NOW + TWENTY_MIN;
    private final String UUID_KEY = "uuid";


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
        validateAccessToken(accessTokenValue);
        AccessToken accessToken = getAccessTokenBy(JwtToken.getUUID(accessTokenValue));
        Member member = getMemberBy(accessToken);
        return MemberInfoDTO.convertFrom(member);
    }

    @Override
    public AccessToken findAccessToken(String accessTokenValue) {
        validateAccessToken(accessTokenValue);
        return getAccessTokenBy(JwtToken.getUUID(accessTokenValue));
    }

    @Override
    public AccessToken makeAccessToken(String refreshToken) {
        JwtToken.validateExpirationTime(refreshToken);
        Long memberId = Long.valueOf(JwtToken.decodeToken(refreshToken, MEMBER_ID));
        String Role = JwtToken.decodeToken(refreshToken, ROLE_KEY);
        AccessToken accessToken = new AccessToken(JwtToken.makeToken(ACCESS_TOKEN_EXP, this.makeUUID()), memberId, makeRole(Role));
        repository.save(accessToken);
        return accessToken;
    }

    private Map<String, Object> makeUUID() {
        HashMap<String, Object> uuidInfo = new HashMap<>();
        uuidInfo.put(UUID_KEY, UUID.randomUUID());
        return uuidInfo;
    }
    @Override
    @Transactional
    public boolean validateAccessToken(String accessTokenValue) {
        try {
            if (!JwtToken.validateExpirationTime(accessTokenValue)) {
                return false;
            }
            return true;
        } catch (JwtException | RedisException e) {
            return false;
        }
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
        return role == accessToken.getRole()
                .getType();
    }
}
