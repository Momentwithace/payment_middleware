package com.payment.account.service;

import com.payment.account.model.Account;
import com.payment.account.repository.AccountRepository;
import com.payment.account.utils.PinEncoder;
import com.payment.shared.dtos.AccountUpdateRequest;
import com.payment.shared.dtos.AccountInternalTransferRequest;
import com.payment.shared.dtos.ResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Optional;

import static com.payment.shared.Constant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountTransactionServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PinEncoder pinEncoder;

    @InjectMocks
    private AccountTransactionServiceImpl accountTransactionService;

    private AccountUpdateRequest validRequest;
    private AccountUpdateRequest invalidRequest;
    private AccountInternalTransferRequest internalTransferRequest;
    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        account = new Account();
        account.setAccountNumber("12345");
        account.setBalance(BigDecimal.valueOf(1000));
        account.setPin("1234");

        validRequest = new AccountUpdateRequest("12345", BigDecimal.valueOf(500), "1234");
        invalidRequest = new AccountUpdateRequest("67890", BigDecimal.valueOf(500), "1234");

        internalTransferRequest = new AccountInternalTransferRequest(validRequest, validRequest);
    }

    @Test
    void testCreditAccount_Success() {
        // Arrange
        when(accountRepository.findByAccountNumber(validRequest.getAccountNumber()))
                .thenReturn(Optional.of(account));

        // Act
        ResponseDto<?> response = accountTransactionService.creditAccount(validRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(SUCCESSFUL, response.getRespDescription());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testCreditAccount_AccountNotFound() {
        // Arrange
        when(accountRepository.findByAccountNumber(invalidRequest.getAccountNumber()))
                .thenReturn(Optional.empty());

        // Act
        ResponseDto<?> response = accountTransactionService.creditAccount(invalidRequest);

//        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatus());
        assertEquals(FAILED, response.getRespDescription());
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    void testDebitAccount_Success() {
        // Arrange
        when(accountRepository.findByAccountNumber(validRequest.getAccountNumber()))
                .thenReturn(Optional.of(account));
        when(pinEncoder.verify(validRequest.getPin(), account.getPin())).thenReturn(true);

        // Act
        ResponseDto<?> response = accountTransactionService.debitAccount(validRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(SUCCESS, response.getRespDescription());
        assertEquals(BigDecimal.valueOf(500), account.getBalance());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testDebitAccount_InsufficientBalance() {
        // Arrange
        AccountUpdateRequest debitRequest = new AccountUpdateRequest("12345", BigDecimal.valueOf(2000), "1234");
        when(accountRepository.findByAccountNumber(debitRequest.getAccountNumber()))
                .thenReturn(Optional.of(account));

        // Act
        ResponseDto<?> response = accountTransactionService.debitAccount(debitRequest);

        // Assert
        assertEquals(FAILED_CODE, response.getRespCode());
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    void testDebitAccount_WrongCredentials() {
        // Arrange
        when(accountRepository.findByAccountNumber(validRequest.getAccountNumber()))
                .thenReturn(Optional.of(account));
        when(pinEncoder.verify(validRequest.getPin(), account.getPin())).thenReturn(false);

        // Act
        ResponseDto<?> response = accountTransactionService.debitAccount(validRequest);

        // Assert
        assertEquals(FAILED_CODE, response.getRespCode());
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    void testInternalTransfer_Success() {
        // Arrange
        when(accountRepository.findByAccountNumber(validRequest.getAccountNumber()))
                .thenReturn(Optional.of(account));
        when(pinEncoder.verify(validRequest.getPin(), account.getPin())).thenReturn(true);
        AccountUpdateRequest toAccountRequest = new AccountUpdateRequest("12345", BigDecimal.valueOf(200), "1234");

        // Act
        ResponseDto<?> response = accountTransactionService.internalTransfer(new AccountInternalTransferRequest(validRequest, toAccountRequest));

        // Assert
        assertEquals(SUCCESSFUL, response.getRespBody());
        verify(accountRepository, times(2)).save(any());
    }
}
