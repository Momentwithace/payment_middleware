package com.payment.payment.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TransferRequest {

    private String bankCode;

    private String sourceAccountNumber;

    private String destinationAccountNumber;

    private String destinationAccountName;

    private String pin;

    private String narration;

    private BigDecimal amount;
}
