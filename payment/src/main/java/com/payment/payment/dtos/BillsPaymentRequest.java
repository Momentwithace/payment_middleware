package com.payment.payment.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillsPaymentRequest {
    private String customerName;
    private String customerAccountNumber;
    private String billerCode;
    private String productCode;
    private BigDecimal amount;

    private String pin;

}
