package com.payment.auth.dtos.request;

import com.payment.auth.dtos.VerificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class VerificationRequest {
    private VerificationType type;
    private String code;
}
