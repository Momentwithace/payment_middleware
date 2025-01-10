package com.payment.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    private String accountName;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance = BigDecimal.ZERO;
    private Long userId;
}
