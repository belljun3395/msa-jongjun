package com.example.web.response;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Response<T> {

    private Timestamp timestamp;
    private int status;
    private String error;
    private String message;
    private T data;

    // todo check how to get request url and fill path
    // private String path;

}
