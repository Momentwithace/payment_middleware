package com.payment.auth.service;

import com.payment.auth.dtos.request.RegistrationRequest;
import com.payment.auth.dtos.request.VerificationRequest;
import com.payment.auth.dtos.response.RegistrationResponse;
import com.payment.auth.dtos.response.TokenResponse;
import com.payment.auth.dtos.response.UserDetailsResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    TokenResponse generateToken(String username);

    RegistrationResponse register(RegistrationRequest registrationRequest);

    UserDetailsResponse getUserDetails(UserDetails userDetails);

    UserDetailsResponse verifyNin(UserDetails userDetail, VerificationRequest verificationRequest);

    Object verifyBvn(UserDetails userDetail, VerificationRequest verificationRequest);
}
