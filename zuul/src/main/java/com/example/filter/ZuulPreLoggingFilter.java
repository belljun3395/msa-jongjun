package com.example.filter;

import com.example.exception.NotValidateTokenExceptionCustom;
import com.example.token.FeignValidateAccessToken;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZuulPreLoggingFilter extends ZuulFilter {

    private final String AUTHORIZATION_HEADER = "Authorization";

    private final FeignValidateAccessToken token;

    @Override
    public Object run() throws ZuulException {
        String uuid = UUID.randomUUID().toString();
        RequestContext context = RequestContext.getCurrentContext();
        context.set("uuid", uuid);
        HttpServletRequest request = context.getRequest();
        String requestURI = request.getRequestURI();
        log.info("REQUEST [{}][{}]", uuid, requestURI);
        return null;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String uri = request.getRequestURI();
        if (uri.matches("/auth/members/.*")) {
            return true;
        }

        String accessToken = request.getHeader(AUTHORIZATION_HEADER);
        if (!token.validateAccessToken(accessToken)) {
            throw new NotValidateTokenExceptionCustom();
        }
        return true;
    }
}

