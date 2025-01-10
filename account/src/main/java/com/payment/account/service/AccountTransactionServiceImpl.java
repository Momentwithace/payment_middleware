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
    private final AccountRepository accountRepository;

    private final PinEncoder pinEncoder;

    @Transactional
    @Override
    public ResponseDto<?> creditAccount(AccountUpdateRequest request) {
        log.info("Crediting account {} with amount {}", request.getAccountNumber(), request.getAmount());
        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(request.getAccountNumber());


        if (optionalAccount.isEmpty()) {
            return new ResponseDto<>(FAILED_CODE, FAILED, ACCOUNT_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        Account account = optionalAccount.get();
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        log.info("Account {} credited successfully. New balance: {}", account.getId(), account.getBalance());
        return new ResponseDto<>(SUCCESS_CODE, SUCCESS, SUCCESSFUL, HttpStatus.OK);

    }

    @Transactional
    @Override
    public ResponseDto<?> debitAccount(AccountUpdateRequest request) {
        log.info("Debiting account {} with amount {}", request.getAccountNumber(), request.getAmount());
        Optional<Account> account = accountRepository.findByAccountNumber(request.getAccountNumber());

        if (account.isEmpty()) {
            return new ResponseDto<>(FAILED_CODE, FAILED, ACCOUNT_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        if (account.get().getBalance().doubleValue() < request.getAmount().doubleValue()) {
            return new ResponseDto<>(FAILED_CODE, FAILED, INSUFFICIENT_BALANCE, HttpStatus.BAD_REQUEST);
        }

        if (!pinEncoder.verify(request.getPin(), account.get().getPin())) {
            return new ResponseDto<>(FAILED_CODE, FAILED, WRONG_CREDENTIALS, HttpStatus.BAD_REQUEST);
        }

        account.get().setBalance(account.get().getBalance().subtract(request.getAmount()));
        accountRepository.save(account.get());

        log.info("Account {} debited successfully. New balance: {}", account.get().getAccountNumber(), account.get().getBalance());

        return new ResponseDto<>(SUCCESS_CODE, SUCCESS, SUCCESSFUL, HttpStatus.OK);

    }

    @Transactional
    @Override
    public ResponseDto<?> internaltransfer(AccountInternalTransferRequest accountInternalTransferRequest) {
        debitAccount(accountInternalTransferRequest.getFromAccountUpdateRequest());
        creditAccount(accountInternalTransferRequest.getToAccountUpdateRequest());
        return new ResponseDto<>(SUCCESS_CODE, SUCCESS, SUCCESSFUL, HttpStatus.OK);
    }
}
