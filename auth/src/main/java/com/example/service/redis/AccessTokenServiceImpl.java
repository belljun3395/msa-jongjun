package com.example.service.redis;

import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.redis.AccessToken;
import com.example.domain.redis.AccessTokenRepository;
import com.example.domain.redis.AccessTokenService;
import com.example.web.dto.MemberInfoDTO;
import com.example.web.exception.MemberValidateError;
import com.example.web.exception.MemberValidateException;
import com.example.web.exception.TokenValidateError;
import com.example.web.exception.TokenValidateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccessTokenServiceImpl implements AccessTokenService {

    private final AccessTokenRepository accessTokenRepository;

    private final MemberRepository memberRepository;


    @Override
    public void save(AccessToken accessToken) {
        accessTokenRepository.save(accessToken);
    }

    @Override
    public MemberInfoDTO browseMatchAccessToken(String accessTokenValue) {
        AccessToken accessToken = getAccessToken(accessTokenValue);
        Member memberInfo = getMemberInfoBy(accessToken);
        return MemberInfoDTO.convertFrom(memberInfo);
    }

    private AccessToken getAccessToken(String accessTokenValue) {
        Optional<AccessToken> byId = accessTokenRepository.findById(accessTokenValue);
        if (byId.isEmpty()) {
            throw new TokenValidateException(TokenValidateError.ACCESS_TIME_EXCEED);
        }
        return byId.get();
    }

    private Member getMemberInfoBy(AccessToken accessToken) {
        Optional<Member> memberInfo = memberRepository.findById(accessToken.getMemberId());
        if (memberInfo.isEmpty()) {
            throw new MemberValidateException(MemberValidateError.NO_EXIST_MEMBER);
        }
        return memberInfo.get();
    }
}
