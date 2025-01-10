package com.payment.payment.service;

import com.payment.payment.Mock;
import com.payment.payment.dtos.BillsPaymentRequest;
import com.payment.payment.dtos.NameLookUpRequest;
import com.payment.payment.dtos.TransferRequest;
import com.payment.payment.model.*;
import com.payment.payment.repository.TransactionRepository;
import com.payment.shared.client.AccountClient;
import com.payment.shared.dtos.AccountInternalTransferRequest;
import com.payment.shared.dtos.AccountUpdateRequest;
import com.payment.shared.dtos.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.payment.shared.Constant.SUCCESS_CODE;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final TransactionRepository transactionRepository;

    private final AccountClient accountClient; //

    private final Mock mock;
    private static final Map<String, List<String>> productsByBiller = new HashMap<>();

    static {
        productsByBiller.put("Electricity", List.of("Prepaid", "Postpaid"));
        productsByBiller.put("Water", List.of("Residential", "Commercial"));
        productsByBiller.put("Internet", List.of("Data Plan", "Fiber Connection"));
        productsByBiller.put("Cable TV", List.of("Basic", "Premium", "Sports Add-On"));
        productsByBiller.put("Airtime", List.of("Top-Up"));
    }

    @Transactional
    @Override
    public ResponseDto<?> transfer(TransferRequest transferRequest) {
        try {


            log.info("Initiating transfer: {}", transferRequest);

            if (transferRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Transfer amount must be greater than zero");
            }

            AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest();
            accountUpdateRequest.setAccountNumber(transferRequest.getSourceAccountNumber());
            accountUpdateRequest.setPin(transferRequest.getPin());
            accountUpdateRequest.setAmount(transferRequest.getAmount());

            AccountUpdateRequest toAccountUpdateRequest = new AccountUpdateRequest();
            toAccountUpdateRequest.setAccountNumber(transferRequest.getDestinationAccountNumber());
            toAccountUpdateRequest.setAmount(transferRequest.getAmount());
            AccountInternalTransferRequest accountInternalTransferRequest =  AccountInternalTransferRequest.builder()
                    .fromAccountUpdateRequest(accountUpdateRequest)
                    .toAccountUpdateRequest(toAccountUpdateRequest)
                    .build();


            ResponseEntity<?> response = accountClient.transfer(accountInternalTransferRequest);


            String transactionId = generateTransactionId();
            Transaction transaction;
            transaction = Transaction.builder()
                    .sourceAccountNumber(transferRequest.getSourceAccountNumber())
                    .destinationAccountNumber(transferRequest.getDestinationAccountNumber())
                    .narration(transferRequest.getNarration())
                    .destinationAccountName(transferRequest.getDestinationAccountName())
                    .amount(transferRequest.getAmount())
                    .transactionId(transactionId)
                    .transactionType(TransactionType.TRANSFER)
                     .createdAt(LocalDateTime.now())
                    .build();
            if (response.getStatusCode().is2xxSuccessful()) {
                transaction.setStatus("SUCCESSFUL");
                transaction = transactionRepository.save(transaction);
                log.info("Transfer successful, Transaction ID: {}", transaction.getTransactionId());
                return new ResponseDto<>("SUCCESSFUL", SUCCESS_CODE, "Transfer Successful", HttpStatus.OK);
            } else {
                transaction.setStatus("FAILED");
            }


            return new ResponseDto<>("Failed", "10", "Transfer failed", HttpStatus.BAD_REQUEST);
        } catch (WebClientResponseException.BadRequest e) {

            return e.getResponseBodyAs(ResponseDto.class);
        }
    }

    public ResponseDto<?> getBanks() {
        return new ResponseDto<>("Success", "0", mock.getBanks(), HttpStatus.OK);
    }

    public ResponseDto<?> accountNameLookUp(NameLookUpRequest nameLookUpRequest) {
        for (OtherAccounts b : mock.getMockOtherAccounts()) {
            if (b.getAccountNumber().equals(nameLookUpRequest.getAccountNumber()) && b.getBankCode().equals(nameLookUpRequest.getBankCode())) {
                return new ResponseDto<>("Success", "0", b.getName(), HttpStatus.OK);
            }
        }
        return new ResponseDto<>("Failed", "10", "Account not found", HttpStatus.BAD_REQUEST);
    }


    public ResponseDto<?> getBillers() {
        return new ResponseDto<>("Success", "0", mock.getBiller(), HttpStatus.OK);
    }


    public ResponseDto<?> getProductsForBiller(String category) {
        List<String> productNames = productsByBiller.getOrDefault(category, new ArrayList<>());
        List<Products> products = new ArrayList<>();
        for (String productName : productNames) {
            products.add(new Products(productName));
        }
        return new ResponseDto<>("Success", "0", products, HttpStatus.OK);
    }

    @Override
    public ResponseDto<?> processBillPayment(BillsPaymentRequest request) {
        if (request.getAmount().doubleValue() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        String transactionId = generateTransactionId();
        AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest();
        accountUpdateRequest.setAccountNumber(request.getCustomerAccountNumber());
        accountUpdateRequest.setPin(request.getPin());
        accountUpdateRequest.setAmount(request.getAmount());
        ResponseEntity<?> response = accountClient.debitAccount(accountUpdateRequest);

        Transaction transaction;
        transaction = Transaction.builder()
                .sourceAccountNumber(request.getCustomerAccountNumber())
                .narration("Bills payment")
                .amount(request.getAmount())
                .transactionId(transactionId)
                .transactionType(TransactionType.BILL)
                .createdAt(LocalDateTime.now())
                .build();
        if (response.getStatusCode().is2xxSuccessful()) {
            transaction.setStatus("SUCCESSFUL");
            transaction = transactionRepository.save(transaction);
            log.info("Transfer successful, Transaction ID: {}", transaction.getTransactionId());
            return new ResponseDto<>("Success", "0", transactionId, HttpStatus.OK);

        } else {
            transaction.setStatus("FAILED");
            transactionRepository.save(transaction);

        }
        return new ResponseDto<>("Failed", "10", "Payment failed", HttpStatus.BAD_REQUEST);


    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
}
