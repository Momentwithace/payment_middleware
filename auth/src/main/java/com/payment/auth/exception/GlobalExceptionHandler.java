package com.payment.auth.exception;

import com.payment.shared.dtos.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<?> handleEmailInUseException(GlobalException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<?> handleWebClientResponseException(ServiceUnavailableException e) {


        return ResponseEntity.status(500).body(
                ErrorResponse.builder()
                        .message(e.getMessage())
                        .error("Internal Server Error")
                        .build()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(BadCredentialsException e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .message("Invalid email or password")
                .build());
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<?> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        e.printStackTrace();
        return ResponseEntity.badRequest()
                .body(ErrorResponse.builder()
                        .message("Invalid email or password")
                        .build()
                );
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleGeneralException(Exception e) {
//        e.printStackTrace();
//        return ResponseEntity.status(500).body(ErrorResponse.builder()
//                .message(e.getMessage())
//               );
//    }
}
