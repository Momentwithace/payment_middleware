package com.payment.account.controller;

import com.payment.account.service.AccountService;
import com.payment.shared.dtos.AccountRequest;
import com.payment.shared.dtos.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/onboard")
    public ResponseEntity<?> onboardCustomer(@RequestBody @Valid AccountRequest accountRequest) {
        log.info("onboard customer: {}", accountRequest);
        ResponseDto<?> resp = accountService.createAccount(accountRequest);
        return new ResponseEntity<>(resp, resp.getHttpStatus());

    }

    @GetMapping("/info")
    public ResponseEntity<?> getAccountInfo(@RequestHeader("Authorization") String authToken) {
        log.info("get account info: {}", authToken);
        ResponseDto<?> resp = accountService.getAccountInfo(authToken);
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    @GetMapping("/get-account")
    public ResponseEntity<?> getAccount(@RequestParam String accountNumber) {
        log.info("get account: {}", accountNumber);
        ResponseDto<?> resp = accountService.getAccount(accountNumber);
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }


}
