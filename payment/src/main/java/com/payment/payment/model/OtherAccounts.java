package com.payment.payment.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OtherAccounts {

    private String name;

    private String accountNumber;

    private String bankCode;
}
