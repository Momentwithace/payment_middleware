package com.payment.payment.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
@RequiredArgsConstructor
public class PaymentRequest {
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
}
