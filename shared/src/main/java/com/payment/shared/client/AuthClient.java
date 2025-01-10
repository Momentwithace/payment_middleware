package com.payment.shared.client;


import com.payment.shared.dtos.UserDetailsResponse;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface AuthClient {
    @GetExchange("/auth/userDetail")
    UserDetailsResponse getAccountDetails(
            @RequestHeader("Authorization") String authToken
    );
}
