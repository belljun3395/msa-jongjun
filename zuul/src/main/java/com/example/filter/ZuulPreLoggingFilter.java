package com.example.filter;

import com.example.token.FeignRenewalToken;
import com.example.token.TokenConsumer;
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

    private final TokenConsumer tokenConsumer;

    private final FeignRenewalToken token;

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
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String uri = request.getRequestURI();
        if (uri.matches("/auth/members") ||
            uri.matches("/auth/members/join") ||
            uri.matches("/auth/members/login") ||
            uri.matches("/auth/members/logout") ||
            uri.matches("/auth/members/token/renewal")
            ) {
            return true;
        }

        String accessToken = request.getHeader(AUTHORIZATION_HEADER);
        if (!tokenConsumer.validateToken(accessToken)) {
            request.setAttribute("Authorization", token.getNewToken());;
        }
        return true;
    }
}

