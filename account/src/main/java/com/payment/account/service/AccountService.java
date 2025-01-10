package com.payment.account.service;

import com.payment.shared.dtos.AccountRequest;
import com.payment.shared.dtos.ResponseDto;
import jakarta.validation.Valid;

public interface AccountService {
    ResponseDto<?> getAccountInfo(String authToken);

    ResponseDto<?> createAccount(@Valid AccountRequest accountRequest);

    ResponseDto<?> getAccount(String accountNumber);
}
