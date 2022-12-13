package com.example.service.token.accessToken;

import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.token.accessToken.AccessToken;
import com.example.domain.token.accessToken.AccessTokenRepository;
import com.example.domain.token.accessToken.AccessTokenService;
import com.example.web.dto.MemberInfoDTO;
import com.example.web.exception.MemberValidateError;
import com.example.web.exception.MemberValidateException;
import com.example.web.exception.TokenValidateError;
import com.example.web.exception.TokenValidateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccessTokenServiceImpl implements AccessTokenService {

    private final AccessTokenRepository accessTokenRepository;

    private final MemberRepository memberRepository;


    @Override
    @Transactional
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
