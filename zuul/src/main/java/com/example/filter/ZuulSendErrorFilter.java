package com.example.filter;

import com.example.exception.CustomZuulException;
import com.example.exception.NotAllowedAPIExceptionCustom;
import com.example.exception.NotValidateTokenExceptionCustom;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

@Slf4j
@Component
public class ZuulSendErrorFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "error";
    }

    @Override
    public int filterOrder() {
        return -1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run()  {

        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        Throwable throwable = context.getThrowable();

        if (throwable instanceof ZuulException) {
            if (throwable.getCause() instanceof NotAllowedAPIExceptionCustom) {
                log.error("Not Allowed Api - " + request.getRequestURI());
                makeErrorResponse(context, request, new NotAllowedAPIExceptionCustom());
            }

            if (throwable.getCause() instanceof NotValidateTokenExceptionCustom) {
                log.error("Not Validate TokenException - " + request.getRequestURI());
                makeErrorResponse(context, request, new NotValidateTokenExceptionCustom());
            }
        }

        return null;
    }

    private static void makeErrorResponse(RequestContext context, HttpServletRequest request, CustomZuulException exception) {
        context.remove("throwable");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("timestamp", new Timestamp(System.currentTimeMillis()));
        jsonObject.put("code", exception.getCode());
        jsonObject.put("error", exception.getError());
        jsonObject.put("message", exception.getErrorMessage());
        jsonObject.put("data", null);
        jsonObject.put("path", request.getRequestURI());

        context.setResponseBody(jsonObject.toJSONString());
        context.getResponse()
                .setContentType("application/json");
        context.setResponseStatusCode(406);  // 406 Not Acceptable
    }
}
