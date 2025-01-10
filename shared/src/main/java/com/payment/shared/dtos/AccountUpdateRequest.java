package com.payment.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class AccountUpdateRequest {
    private String accountNumber;
    private BigDecimal amount;
    private String pin;
}
