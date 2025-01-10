package com.payment.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class AccountInternalTransferRequest {
    private AccountUpdateRequest fromAccountUpdateRequest;
    private AccountUpdateRequest toAccountUpdateRequest;
}
