package com.example.filter;

import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZuulPostLoggingFilter extends ZuulFilter {
    public static final String LOG_ID = "logId";


    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        Object uuid = context.get("uuid");
        HttpServletRequest request = context.getRequest();
        String requestURI = request.getRequestURI();
        log.info("RESPONSE [{}][{}]", uuid, requestURI);

        return null;
    }

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        // todo check is this right?
        // todo request header authorization keep
        RequestContext context = RequestContext.getCurrentContext();
        List<Pair<String, String>> originResponseHeaders = context.getOriginResponseHeaders();
        HttpServletResponse response = context.getResponse();
        for (Pair<String, String> p : originResponseHeaders) {
            if (p.first().equals("Authorization") || p.first().equals("Set-Cookie")) {
                response.setHeader(p.first(), p.second());
            }
        }
        context.setResponse(response);
        return true;
    }

}

