package com.payment.auth.dtos.request;

import com.payment.auth.myannotation.ValidEmailAddress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    @ValidEmailAddress
    private String email;
    private String password;

}
