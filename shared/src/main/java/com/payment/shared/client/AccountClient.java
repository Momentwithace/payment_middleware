package com.payment.shared.client;


import com.payment.shared.dtos.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface AccountClient {

    @PostExchange("/account/onboard")
    ResponseDto<AccountResponse> createAccount(
            @RequestBody AccountRequest accountRequest);

    @GetExchange("/account/get-account")
    AccountResponse getAccount( @RequestParam("accountNumber") String accountNumber);

    @PostExchange("/account/debit")
    ResponseEntity<?> debitAccount(@RequestBody AccountUpdateRequest accountUpdateRequest);

    @PostExchange("/account/credit")
    ResponseEntity<?>  creditAccount(@RequestBody AccountUpdateRequest accountUpdateRequest);

    @PostExchange("/account/transfer")
    ResponseEntity<?>  transfer(@RequestBody AccountInternalTransferRequest accountInternalTransferRequest);


}
