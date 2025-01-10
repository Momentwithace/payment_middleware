package com.payment.account.service;

import com.payment.shared.dtos.AccountUpdateRequest;
import com.payment.shared.dtos.AccountInternalTransferRequest;
import com.payment.shared.dtos.ResponseDto;

public interface AccountTransactionService {
    ResponseDto<?> creditAccount(AccountUpdateRequest request);
    ResponseDto<?> debitAccount(AccountUpdateRequest request);

    ResponseDto<?> internaltransfer(AccountInternalTransferRequest accountInternalTransferRequest);
}
