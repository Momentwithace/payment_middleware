package com.payment.auth.exception;

public class ServiceUnavailableException  extends RuntimeException{
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
