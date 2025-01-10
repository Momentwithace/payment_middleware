package com.payment.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class AccountRequest {
    private String nin;
    private String bvn;
    private String accountName;
    private Long userId;
    private String pin;
}
