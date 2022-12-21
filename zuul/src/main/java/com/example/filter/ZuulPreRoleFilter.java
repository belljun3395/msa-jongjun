package com.example.filter;

import com.example.exception.NotAllowedAPIExceptionCustom;
import com.example.token.FeignValidateAccessToken;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class ZuulPreRoleFilter extends ZuulFilter {
    private final String AUTHORIZATION_HEADER = "Authorization";
    private final FeignValidateAccessToken token;

    @Override
    public Object run() throws ZuulException {
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
        String accessToken = request.getHeader(AUTHORIZATION_HEADER);
        if (uri.contains("admin")) {
            context.set("role", "admin");
            if (!token.validateAccessTokenRole(accessToken, "admin")) {
                throw new NotAllowedAPIExceptionCustom();
            }
            return true;
        }
        return true;
    }
}
