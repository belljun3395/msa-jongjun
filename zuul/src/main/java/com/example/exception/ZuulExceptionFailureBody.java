package com.example.exception;

import lombok.Getter;
import net.minidev.json.JSONObject;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter
public class ZuulExceptionFailureBody implements Serializable {
    private Timestamp timestamp;
    private int code;
    private String error;
    private String message;

    public ZuulExceptionFailureBody(final int code, final String error, final String message) {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.code = code;
        this.error = error;
        this.message = message;
    }

    public JSONObject convertToJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("timestamp", this.timestamp);
        jsonObject.put("code", this.code);
        jsonObject.put("error", this.error);
        jsonObject.put("message", this.message);
        return jsonObject;
    }
}
