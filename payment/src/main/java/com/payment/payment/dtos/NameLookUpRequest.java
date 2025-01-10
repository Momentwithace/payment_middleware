package com.payment.payment.dtos;

import lombok.Data;

@Data
public class NameLookUpRequest {

    private String accountNumber;

    private String bankCode;
}
