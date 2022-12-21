package com.example.web.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter
public class ApiResponse<T> extends ResponseEntity<T> {

    public ApiResponse(T body, HttpStatus status) {
        super(body, status);
    }


    @Getter
    public static class FailureBody implements Serializable {
        private Timestamp timestamp;
        private int code;
        private String error;
        private String message;

        public FailureBody(final int code, final String error, final String message) {
            this.timestamp = new Timestamp(System.currentTimeMillis());
            this.code = code;
            this.error = error;
            this.message = message;
        }
    }

    @Getter
    public static class withData<T> implements Serializable {
        private Timestamp timestamp;
        private int code;
        private String message;
        private T data;

        public withData(T data,int code, String message) {
            this.timestamp = new Timestamp(System.currentTimeMillis());
            this.code = code;
            this.message = message;
            this.data = data;
        }
    }
    @Getter
    public static class withCodeAndMessage implements Serializable {
        private Timestamp timestamp;
        private int code;
        private String message;

        public withCodeAndMessage(int code, String message) {
            this.timestamp = new Timestamp(System.currentTimeMillis());
            this.code = code;
            this.message = message;
        }
    }
}
