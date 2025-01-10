package com.payment.account.service;

import com.payment.account.utils.PinEncoder;
import com.payment.shared.dtos.*;
import com.payment.shared.client.AuthClient;
import com.payment.account.dtos.*;
import com.payment.account.model.Account;
import com.payment.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.payment.account.utils.AccountUtil.generateAccountNumber;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AuthClient authClient;
    private final ModelMapper modelMapper;
    private final PinEncoder pinEncoder;



    @Override
    public ResponseDto<?> getAccountInfo(String authToken) {
        UserDetailsResponse userDetailsResponse = authClient.getAccountDetails(authToken);

        List<Account> accounts = accountRepository.findAllByUserId(userDetailsResponse.getId());
        List<AccountResponse> accountResponses = accounts.stream().map(account -> modelMapper.map(account, AccountResponse.class)).toList();
        DashboardResponse dashboardResponse = DashboardResponse.builder()
                .accounts(accountResponses)
                .user(userDetailsResponse)
                .build();
        return new ResponseDto<>("Success", "0", dashboardResponse, HttpStatus.OK);

    }


    @Override
    public ResponseDto<?> createAccount(AccountRequest accountRequest) {
        String accountNumber = generateAccountNumber(AccountType.SAVINGS);
        Account account = Account.builder()
                .accountName(accountRequest.getAccountName())
                .accountNumber(accountNumber)
                .accountType(AccountType.SAVINGS)
                // for testing purposes
                .balance(BigDecimal.valueOf(10000))
                .pin(pinEncoder.encode(accountRequest.getPin()))
                .userId(accountRequest.getUserId())
                .build();
        accountRepository.save(account);
        AccountResponse accountResponse = AccountResponse.builder()
                .accountNumber(accountNumber)
                .accountType(account.getAccountType())
                .build();

        return new ResponseDto<>("Success", "0", accountResponse, HttpStatus.OK);

    }

    @Override
    public ResponseDto<?> getAccount(String accountNumber) {

        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        if (account.isEmpty()) {
            return new ResponseDto<>("Failed", "10", "Account not found", HttpStatus.BAD_REQUEST);

        }
        AccountResponse accountResponse = modelMapper.map(account.get(), AccountResponse.class);

        return new ResponseDto<>("Success", "0", accountResponse, HttpStatus.OK);

    }
}
