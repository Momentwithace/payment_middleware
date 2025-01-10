package com.payment.account.service;

import com.payment.account.dtos.*;
import com.payment.account.model.Account;
import com.payment.account.repository.AccountRepository;
import com.payment.account.utils.AccountUtil;
import com.payment.account.utils.PinEncoder;
import com.payment.shared.client.AuthClient;
import com.payment.shared.dtos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
//import static org.powermock.api.mockito.PowerMockito.mockStatic;

class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AuthClient authClient;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PinEncoder pinEncoder;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAccountInfo() {
        // Mock data
        String authToken = "test-token";
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
        userDetailsResponse.setId(123L);

        Account account = new Account();
        account.setAccountNumber("1234567890");
        account.setAccountType(AccountType.SAVINGS);

        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setAccountNumber("1234567890");
        accountResponse.setAccountType(AccountType.SAVINGS);

        DashboardResponse dashboardResponse = DashboardResponse.builder()
                .accounts(List.of(accountResponse))
                .user(userDetailsResponse)
                .build();

        // Mock behavior
        when(authClient.getAccountDetails(authToken)).thenReturn(userDetailsResponse);
        when(accountRepository.findAllByUserId(123L)).thenReturn(List.of(account));
        when(modelMapper.map(account, AccountResponse.class)).thenReturn(accountResponse);

        // Call service
        ResponseDto<?> response = accountService.getAccountInfo(authToken);

        // Assertions
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(dashboardResponse, response.getRespBody());
        verify(authClient, times(1)).getAccountDetails(authToken);
        verify(accountRepository, times(1)).findAllByUserId(123L);
    }

    @Test
    void testCreateAccount() {
        // Mock data
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAccountName("Test Account");
        accountRequest.setPin("1234");
        accountRequest.setUserId(13L);

        String encodedPin = "encoded-1234";
        String accountNumber = "1234567890";

        AccountResponse expectedResponse = new AccountResponse();
        expectedResponse.setAccountNumber(accountNumber);
        expectedResponse.setAccountType(AccountType.SAVINGS);
        expectedResponse.setBalance(BigDecimal.valueOf(10000));

        Account account = Account.builder()
                .accountName("Test Account")
                .accountNumber(accountNumber)
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.valueOf(10000))
                .pin(encodedPin)
                .userId(13L)
                .build();

        // Mock behavior
        mockStatic(AccountUtil.class);
        when(AccountUtil.generateAccountNumber(AccountType.SAVINGS)).thenReturn(accountNumber);
        when(pinEncoder.encode(accountRequest.getPin())).thenReturn(encodedPin);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // Call service
        ResponseDto<?> response = accountService.createAccount(accountRequest);

        // Verify HttpStatus
        assertEquals(HttpStatus.OK, response.getHttpStatus());

        // Verify response body manually
        AccountResponse responseBody = (AccountResponse) response.getRespBody();
        assertEquals(expectedResponse.getAccountNumber(), responseBody.getAccountNumber());
        assertEquals(expectedResponse.getAccountType(), responseBody.getAccountType());

        // Verify save was called once
        verify(accountRepository, times(1)).save(any(Account.class));
    }



    @Test
    void testGetAccountFound() {
        // Mock data
        String accountNumber = "1234567890";
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setAccountType(AccountType.SAVINGS);

        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setAccountNumber(accountNumber);
        accountResponse.setAccountType(AccountType.SAVINGS);

        // Mock behavior
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
        when(modelMapper.map(account, AccountResponse.class)).thenReturn(accountResponse);

        // Call service
        ResponseDto<?> response = accountService.getAccount(accountNumber);

        // Assertions
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(accountResponse, response.getRespBody());
        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
    }

    @Test
    void testGetAccountNotFound() {
        // Mock data
        String accountNumber = "1234567890";

        // Mock behavior
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        // Call service
        ResponseDto<?> response = accountService.getAccount(accountNumber);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatus());
        assertEquals("Account Not Found", response.getRespBody());
        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
    }
}
