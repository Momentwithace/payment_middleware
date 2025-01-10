package com.payment.account.dtos;

import com.payment.shared.dtos.AccountResponse;
import com.payment.shared.dtos.UserDetailsResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class DashboardResponse {
    List<AccountResponse> accounts;
    UserDetailsResponse user;

}
