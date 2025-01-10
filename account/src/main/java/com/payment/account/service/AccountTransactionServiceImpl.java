package com.payment.account.service;

import com.payment.account.model.Account;
import com.payment.account.repository.AccountRepository;
import com.payment.account.utils.PinEncoder;
import com.payment.shared.dtos.AccountUpdateRequest;
import com.payment.shared.dtos.AccountInternalTransferRequest;
import com.payment.shared.dtos.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.payment.shared.Constant.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountTransactionServiceImpl implements AccountTransactionService {

    // Dependencies injected via constructor
    private final AccountRepository accountRepository;
    private final PinEncoder pinEncoder;

    /**
     * Credits the specified amount to the account.
     *
     * @param request The request containing account number and amount to be credited.
     * @return A ResponseDto containing the status of the operation.
     */
    @Transactional
    @Override
    public ResponseDto<?> creditAccount(AccountUpdateRequest request) {
        log.info("Crediting account {} with amount {}", request.getAccountNumber(), request.getAmount());

        // Retrieve the account by account number
        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(request.getAccountNumber());

        // If account is not found, return an error response
        if (optionalAccount.isEmpty()) {
            return new ResponseDto<>(FAILED_CODE, FAILED, ACCOUNT_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        // Account is found, update its balance by adding the credit amount
        Account account = optionalAccount.get();
        account.setBalance(account.getBalance().add(request.getAmount()));

        // Save the updated account information
        accountRepository.save(account);

        log.info("Account {} credited successfully. New balance: {}", account.getId(), account.getBalance());

        // Return a success response
        return new ResponseDto<>(SUCCESS_CODE, SUCCESSFUL, SUCCESS, HttpStatus.OK);
    }

    /**
     * Debits the specified amount from the account after verifying the account's balance and PIN.
     *
     * @param request The request containing account number, amount, and PIN for the debit operation.
     * @return A ResponseDto containing the status of the operation.
     */
    @Transactional
    @Override
    public ResponseDto<?> debitAccount(AccountUpdateRequest request) {
        log.info("Debiting account {} with amount {}", request.getAccountNumber(), request.getAmount());

        // Retrieve the account by account number
        Optional<Account> account = accountRepository.findByAccountNumber(request.getAccountNumber());

        // If account is not found, return an error response
        if (account.isEmpty()) {
            return new ResponseDto<>(FAILED_CODE, FAILED, ACCOUNT_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        // If account has insufficient balance, return an error response
        if (account.get().getBalance().doubleValue() < request.getAmount().doubleValue()) {
            return new ResponseDto<>(FAILED_CODE, FAILED, INSUFFICIENT_BALANCE, HttpStatus.BAD_REQUEST);
        }

        // Verify that the provided PIN matches the stored PIN for the account
        if (!pinEncoder.verify(request.getPin(), account.get().getPin())) {
            return new ResponseDto<>(FAILED_CODE, FAILED, WRONG_CREDENTIALS, HttpStatus.BAD_REQUEST);
        }

        // Account is valid, deduct the specified amount from the balance
        account.get().setBalance(account.get().getBalance().subtract(request.getAmount()));

        // Save the updated account information
        accountRepository.save(account.get());

        log.info("Account {} debited successfully. New balance: {}", account.get().getAccountNumber(), account.get().getBalance());

        // Return a success response
        return new ResponseDto<>(SUCCESS_CODE, SUCCESS, SUCCESSFUL, HttpStatus.OK);
    }

    /**
     * Performs an internal transfer between two accounts (debits one account and credits another).
     *
     * @param accountInternalTransferRequest The request containing details of both accounts for the transfer.
     * @return A ResponseDto containing the status of the transfer operation.
     */
    @Transactional
    @Override
    public ResponseDto<?> internalTransfer(AccountInternalTransferRequest accountInternalTransferRequest) {
        // First, debit the 'from' account
        debitAccount(accountInternalTransferRequest.getFromAccountUpdateRequest());

        // Then, credit the 'to' account
        creditAccount(accountInternalTransferRequest.getToAccountUpdateRequest());

        // Return a success response indicating the transfer was completed successfully
        return new ResponseDto<>(SUCCESS_CODE, SUCCESS, SUCCESSFUL, HttpStatus.OK);
    }
}
