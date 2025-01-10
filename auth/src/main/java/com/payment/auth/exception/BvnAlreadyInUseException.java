package com.payment.auth.exception;

public class BvnAlreadyInUseException extends RuntimeException {
    public BvnAlreadyInUseException(String message) {
        super(message);
    }
}
