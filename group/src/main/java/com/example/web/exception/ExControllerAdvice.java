package com.example.web.exception;

import com.example.web.response.ApiResponse;
import com.example.web.response.ApiResponseGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalStateException.class})
    public ApiResponse<ApiResponse.FailureBody> illegalStateExHandler(IllegalStateException exception) {
        logger(exception);
        String defaultMessage = exception.getMessage();
        return ApiResponseGenerator.fail(HttpStatus.BAD_REQUEST, 1001,
                exception.getClass()
                        .getSimpleName(), defaultMessage);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public List<ApiResponse<ApiResponse.FailureBody>> bindingExHandler(BindException exception) {
        logger(exception);
        List<ApiResponse<ApiResponse.FailureBody>> errorResults = new ArrayList<>();
        exception.getAllErrors()
                .forEach(error -> {
                    FieldError fieldError = (FieldError) error;
                    String field = fieldError.getField();
                    String defaultMessage = fieldError.getDefaultMessage();
                    ApiResponse<ApiResponse.FailureBody> bindingException = ApiResponseGenerator.fail(HttpStatus.BAD_REQUEST, 1002, exception.getClass()
                            .getSimpleName(), field + " 필드 입력이 필요합니다. " + defaultMessage);
                    errorResults.add(bindingException);
                });
        return errorResults;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({GroupValidateException.class})
    public ApiResponse<ApiResponse.FailureBody> TokenValidateExHandler(GroupValidateException exception) {
        logger(exception);
        String defaultMessage = exception.getMessage();
        return ApiResponseGenerator.fail(HttpStatus.BAD_REQUEST, exception.getCode(),
                exception.getClass().getSimpleName(), defaultMessage);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({GroupMemberValidateException.class})
    public ApiResponse<ApiResponse.FailureBody> TokenValidateExHandler(GroupMemberValidateException exception) {
        logger(exception);
        String defaultMessage = exception.getMessage();
        return ApiResponseGenerator.fail(HttpStatus.BAD_REQUEST, exception.getCode(),
                exception.getClass().getSimpleName(), defaultMessage);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ApiResponse<ApiResponse.FailureBody> exHandler(Exception exception) {
        logger(exception);
        String defaultMessage = exception.getMessage();
        return ApiResponseGenerator.fail(HttpStatus.BAD_REQUEST, 1000,
                exception.getClass().getSimpleName(), defaultMessage);
    }

    private static void logger(Exception exception) {
        log.error(exception.getClass().getSimpleName() + " = [{}][{}]",
                            exception.getClass(), exception.getMessage());
    }
}
