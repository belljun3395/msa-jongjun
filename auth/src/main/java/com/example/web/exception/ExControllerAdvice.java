package com.example.web.exception;

import com.example.web.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils.Null;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalStateException.class})
    public ApiResponse<Null> illegalStateExHandler(IllegalStateException exception) {
        log.info("IllegalStateException = [{}][{}]", exception.getClass(), exception.getMessage());
        String defaultMessage = exception.getMessage();
        return new ApiResponse<>(1001, "IllegalStateException", defaultMessage);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public List<ApiResponse<Null>> bindingExHandler(BindException exception) {
        log.error("BindException = [{}][{}]", exception.getClass(), exception.getMessage());
        List<ApiResponse<Null>> errorResults = new ArrayList<>();
        exception.getAllErrors()
                .forEach(error -> {
                    FieldError fieldError = (FieldError) error;
                    String field = fieldError.getField();
                    String defaultMessage = fieldError.getDefaultMessage();
                    ApiResponse<Null> bindingException = new ApiResponse<>(1002, "BindingException", field + " 필드 입력이 필요합니다. " + defaultMessage);
                    errorResults.add(bindingException);
                });
        return errorResults;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MemberValidateException.class})
    public ResponseEntity<ApiResponse<Null>> MemberValidateExHandler(MemberValidateException exception, WebRequest request) {
        log.error("MemberValidateException = [{}][{}]", exception.getClass(), exception.getMessage());
        String defaultMessage = exception.getMessage();
        ApiResponse<Null> memberValidateException = new ApiResponse<>(exception.getCode(), "MemberValidateException", defaultMessage);
        memberValidateException.setPath(request);
        return new ResponseEntity<>(memberValidateException, HttpStatus.ACCEPTED);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({TokenValidateException.class})
    public ResponseEntity<ApiResponse<Null>> TokenValidateExHandler(TokenValidateException exception, WebRequest request) {
        log.error("TokenValidateException = [{}][{}]", exception.getClass(), exception.getMessage());
        String defaultMessage = exception.getMessage();
        ApiResponse<Null> tokenValidateException = new ApiResponse<>(exception.getCode(), "TokenValidateException", defaultMessage);
        tokenValidateException.setPath(request);
        return new ResponseEntity<>(tokenValidateException, HttpStatus.ACCEPTED);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ApiResponse<Null> exHandler(Exception exception) {
        log.error("Exception = [{}][{}]", exception.getClass(), exception.getMessage());
        String defaultMessage = exception.getMessage();
        return new ApiResponse<>(1000, "Exception", defaultMessage);
    }
}
