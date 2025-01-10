package com.payment.auth.exception;

public class NInAlreadyInUseException extends RuntimeException {
        public NInAlreadyInUseException (String message) {
            super(message);
        }
}
