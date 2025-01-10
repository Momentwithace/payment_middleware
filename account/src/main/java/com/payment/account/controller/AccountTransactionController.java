package com.payment.account.controller;

import com.payment.account.service.AccountTransactionService;
import com.payment.shared.dtos.AccountUpdateRequest;
import com.payment.shared.dtos.AccountInternalTransferRequest;
import com.payment.shared.dtos.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountTransactionController {
    private final AccountTransactionService accountTransactionService;

    @PostMapping("/credit")
    public ResponseEntity<?> creditAccount(@RequestBody AccountUpdateRequest request) {
        ResponseDto<?> resp = accountTransactionService.creditAccount(request);
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    @PostMapping("/debit")
    public ResponseEntity<?> debitAccount(@RequestBody AccountUpdateRequest request) {
        ResponseDto<?> resp = accountTransactionService.debitAccount(request);
        return new ResponseEntity<>(resp, resp.getHttpStatus());
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody AccountInternalTransferRequest request) {
        return ResponseEntity.ok(accountTransactionService.internaltransfer(request));
    }

}
