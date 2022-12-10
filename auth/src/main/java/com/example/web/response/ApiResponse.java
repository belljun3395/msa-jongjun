package com.example.web.response;

import lombok.Getter;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

@Getter
public class ApiResponse<T> {

    private Timestamp timestamp;
    private int code;
    private String error;
    private String message;
    private T data;
    private String path;

    public ApiResponse(int code, String message, T data) {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(int code, String message) {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.code = code;
        this.message = message;
    }

    public ApiResponse(int code, String error, String message) {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.code = code;
        this.error = error;
        this.message = message;
    }

    public ApiResponse<T> setPath(WebRequest webRequest) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) webRequest;
        HttpServletRequest servletRequest = servletWebRequest.getRequest();
        this.path = servletRequest.getRequestURI();
        return this;
    }

}
