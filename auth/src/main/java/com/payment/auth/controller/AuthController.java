package com.payment.auth.controller;


import com.payment.auth.dtos.request.AuthRequest;
import com.payment.auth.dtos.request.RegistrationRequest;
import com.payment.auth.dtos.request.VerificationRequest;
import com.payment.auth.dtos.response.RegistrationResponse;
import com.payment.auth.dtos.response.TokenResponse;
import com.payment.auth.dtos.response.UserDetailsResponse;
import com.payment.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> addNewUser(@RequestBody RegistrationRequest registrationRequest) {
        log.info("Add new user");
        return ResponseEntity.ok(authService.register(registrationRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> getToken(@RequestBody AuthRequest authRequest) {
        log.info("Logging in {}" ,authRequest.getEmail());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

        return ResponseEntity.ok(authService.generateToken(authRequest.getEmail()));

    }

    @PostMapping("/verifyNin")
    public ResponseEntity<?> verifyNin(
            @AuthenticationPrincipal UserDetails userDetail,
            @RequestBody VerificationRequest verificationRequest
    ) {
        return ResponseEntity.ok(authService.verifyNin(userDetail, verificationRequest));
    }

    @PostMapping("/verifyBvn")
    public ResponseEntity<?> verifyBvn(
            @AuthenticationPrincipal UserDetails userDetail,
            @RequestBody VerificationRequest verificationRequest) {
        return ResponseEntity.ok(authService.verifyBvn(userDetail, verificationRequest));
    }


    @GetMapping("/userDetail")
    public ResponseEntity<UserDetailsResponse> validateToken(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.getUserDetails(userDetails));
    }

}
