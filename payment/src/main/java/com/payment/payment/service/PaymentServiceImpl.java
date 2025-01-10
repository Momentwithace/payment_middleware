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

import static com.payment.shared.Constant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    // Injected dependencies: transaction repository, account client, and mock data for testing
    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;
    private final Mock mock;

    // A map for storing billers and their associated product categories
    private static final Map<String, List<String>> productsByBiller = new HashMap<>();

    // Static block to populate the product categories for each biller type
    static {
        productsByBiller.put("Electricity", List.of("Prepaid", "Postpaid"));
        productsByBiller.put("Water", List.of("Residential", "Commercial"));
        productsByBiller.put("Internet", List.of("Data Plan", "Fiber Connection"));
        productsByBiller.put("Cable TV", List.of("Basic", "Premium", "Sports Add-On"));
        productsByBiller.put("Airtime", List.of("Top-Up"));
    }

    /**
     * Handles the transfer of funds between accounts.
     *
     * @param transferRequest The request containing the source and destination account details along with the amount.
     * @return A ResponseDto containing the status of the transfer operation.
     */
    @Transactional
    @Override
    public ResponseDto<?> transfer(TransferRequest transferRequest) {
        try {
            log.info("Initiating transfer: {}", transferRequest);

            // Validate that the amount is greater than zero
            if (transferRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(AMOUNT_MUST_BE_GREATER_THAN_ZERO);
            }

            // Prepare request objects for updating both source and destination account balances
            AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest();
            accountUpdateRequest.setAccountNumber(transferRequest.getSourceAccountNumber());
            accountUpdateRequest.setPin(transferRequest.getPin());
            accountUpdateRequest.setAmount(transferRequest.getAmount());

            AccountUpdateRequest toAccountUpdateRequest = new AccountUpdateRequest();
            toAccountUpdateRequest.setAccountNumber(transferRequest.getDestinationAccountNumber());
            toAccountUpdateRequest.setAmount(transferRequest.getAmount());

            // Create an internal transfer request object
            AccountInternalTransferRequest accountInternalTransferRequest =  AccountInternalTransferRequest.builder()
                    .fromAccountUpdateRequest(accountUpdateRequest)
                    .toAccountUpdateRequest(toAccountUpdateRequest)
                    .build();

            // Call the account client to process the transfer
            ResponseEntity<?> response = accountClient.transfer(accountInternalTransferRequest);

            // Generate a transaction ID and save the transaction details
            String transactionId = generateTransactionId();
            Transaction transaction = Transaction.builder()
                    .sourceAccountNumber(transferRequest.getSourceAccountNumber())
                    .destinationAccountNumber(transferRequest.getDestinationAccountNumber())
                    .narration(transferRequest.getNarration())
                    .destinationAccountName(transferRequest.getDestinationAccountName())
                    .amount(transferRequest.getAmount())
                    .transactionId(transactionId)
                    .transactionType(TransactionType.TRANSFER)
                    .createdAt(LocalDateTime.now())
                    .build();

            // If the transfer is successful, save the transaction and return a success response
            if (response.getStatusCode().is2xxSuccessful()) {
                transaction.setStatus(SUCCESSFUL);
                transaction = transactionRepository.save(transaction);
                log.info("Transfer successful, Transaction ID: {}", transaction.getTransactionId());
                return new ResponseDto<>(SUCCESS_CODE, SUCCESS_CODE, TRANSFER_SUCCESS, HttpStatus.OK);
            } else {
                // If the transfer fails, set the status to FAILED
                transaction.setStatus(FAILED);
            }

            // Return a failure response if transfer did not succeed
            return new ResponseDto<>(FAILED_CODE, FAILED, TRANSFER_FAILED, HttpStatus.BAD_REQUEST);
        } catch (WebClientResponseException.BadRequest e) {
            // Catch and return the response body from the exception
            return e.getResponseBodyAs(ResponseDto.class);
        }
    }

    /**
     * Retrieves the list of available banks.
     *
     * @return A ResponseDto containing the list of banks.
     */
    public ResponseDto<?> getBanks() {
        return new ResponseDto<>(SUCCESS_CODE, SUCCESS, mock.getBanks(), HttpStatus.OK);
    }

    /**
     * Looks up the account holder's name based on the account number and bank code.
     *
     * @param nameLookUpRequest The request containing the account number and bank code.
     * @return A ResponseDto with the account holder's name or an error message if not found.
     */
    public ResponseDto<?> accountNameLookUp(NameLookUpRequest nameLookUpRequest) {
        // Search for the account in mock data
        for (OtherAccounts b : mock.getMockOtherAccounts()) {
            if (b.getAccountNumber().equals(nameLookUpRequest.getAccountNumber()) && b.getBankCode().equals(nameLookUpRequest.getBankCode())) {
                return new ResponseDto<>(SUCCESS_CODE, SUCCESS, b.getName(), HttpStatus.OK);
            }
        }
        // Return failure if account is not found
        return new ResponseDto<>(FAILED_CODE, FAILED, ACCOUNT_NOT_FOUND, HttpStatus.BAD_REQUEST);
    }

    /**
     * Retrieves the list of available billers.
     *
     * @return A ResponseDto containing the list of billers.
     */
    public ResponseDto<?> getBillers() {
        return new ResponseDto<>(SUCCESS_CODE, SUCCESS, mock.getBiller(), HttpStatus.OK);
    }

    /**
     * Retrieves the list of products available for a given biller category.
     *
     * @param category The category of the biller (e.g., "Electricity", "Water").
     * @return A ResponseDto containing the list of products for the biller category.
     */
    public ResponseDto<?> getProductsForBiller(String category) {
        // Fetch products based on the biller category
        List<String> productNames = productsByBiller.getOrDefault(category, new ArrayList<>());
        List<Products> products = new ArrayList<>();
        for (String productName : productNames) {
            products.add(new Products(productName));
        }
        return new ResponseDto<>(SUCCESS_CODE, SUCCESS, products, HttpStatus.OK);
    }

    /**
     * Processes a bill payment by debiting the customer's account and saving the transaction.
     *
     * @param request The bill payment request containing payment details.
     * @return A ResponseDto containing the status of the bill payment operation.
     */
    @Override
    public ResponseDto<?> processBillPayment(BillsPaymentRequest request) {
        // Validate that the amount is greater than zero
        if (request.getAmount().doubleValue() <= 0) {
            throw new IllegalArgumentException(AMOUNT_MUST_BE_GREATER_THAN_ZERO);
        }

        // Generate a transaction ID
        String transactionId = generateTransactionId();

        // Prepare account update request to debit the customer's account
        AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest();
        accountUpdateRequest.setAccountNumber(request.getCustomerAccountNumber());
        accountUpdateRequest.setPin(request.getPin());
        accountUpdateRequest.setAmount(request.getAmount());

        // Call the account client to process the debit
        ResponseEntity<?> response = accountClient.debitAccount(accountUpdateRequest);

        // Save the bill payment transaction
        Transaction transaction = Transaction.builder()
                .sourceAccountNumber(request.getCustomerAccountNumber())
                .narration(BILL_PAYMENT)
                .amount(request.getAmount())
                .transactionId(transactionId)
                .transactionType(TransactionType.BILL)
                .createdAt(LocalDateTime.now())
                .build();

        // If the bill payment is successful, save the transaction and return a success response
        if (response.getStatusCode().is2xxSuccessful()) {
            transaction.setStatus(SUCCESSFUL);
            transaction = transactionRepository.save(transaction);
            log.info("Transfer successful, Transaction ID: {}", transaction.getTransactionId());
            return new ResponseDto<>(SUCCESS_CODE, SUCCESS, transactionId, HttpStatus.OK);
        } else {
            // If the bill payment fails, set the status to FAILED
            transaction.setStatus(FAILED);
            transactionRepository.save(transaction);
        }

        // Return failure response if bill payment failed
        return new ResponseDto<>(FAILED_CODE, FAILED, PAYMENT_FAILED, HttpStatus.BAD_REQUEST);
    }

    /**
     * Generates a unique transaction ID.
     *
     * @return A unique transaction ID as a string.
     */
    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
}
