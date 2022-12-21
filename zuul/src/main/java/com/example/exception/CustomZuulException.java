package com.example.exception;

public abstract class CustomZuulException extends RuntimeException {


    public abstract int getCode();


    public abstract String getError();

    public  abstract String getErrorMessage();
}
