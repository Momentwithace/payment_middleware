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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.payment.account.utils.AccountUtil.generateAccountNumber;
import static com.payment.shared.Constant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    // Dependencies injected via constructor
    private final AccountRepository accountRepository;
    private final AuthClient authClient;
    private final ModelMapper modelMapper;
    private final PinEncoder pinEncoder;

    /**
     * Retrieves account information for a user based on the provided authorization token.
     *
     * @param authToken The authentication token of the user.
     * @return A ResponseDto containing user details and account information.
     */
    @Override
    public ResponseDto<?> getAccountInfo(String authToken) {
        // Fetch user details from the authentication service using the auth token
        UserDetailsResponse userDetailsResponse = authClient.getAccountDetails(authToken);

        // Retrieve all accounts associated with the user
        List<Account> accounts = accountRepository.findAllByUserId(userDetailsResponse.getId());

        // Map account entities to account response DTOs
        List<AccountResponse> accountResponses = accounts.stream()
                .map(account -> modelMapper.map(account, AccountResponse.class))
                .toList();

        // Build a DashboardResponse that includes user details and accounts
        DashboardResponse dashboardResponse = DashboardResponse.builder()
                .accounts(accountResponses)
                .user(userDetailsResponse)
                .build();

        // Return the success response with the dashboard data
        return new ResponseDto<>(SUCCESS_CODE, SUCCESS, dashboardResponse, HttpStatus.OK);
    }

    /**
     * Creates a new account with the given account details.
     *
     * @param accountRequest The request object containing account creation details.
     * @return A ResponseDto containing the created account's information.
     */
    @Override
    public ResponseDto<?> createAccount(AccountRequest accountRequest) {
        // Generate a new account number
        String accountNumber = generateAccountNumber(AccountType.SAVINGS);

        // Build a new Account entity from the account request data
        Account account = Account.builder()
                .accountName(accountRequest.getAccountName())
                .accountNumber(accountNumber)
                .accountType(AccountType.SAVINGS) // Assuming a default account type of SAVINGS
                .balance(BigDecimal.valueOf(10000)) // For testing purposes, a default balance is set
                .pin(pinEncoder.encode(accountRequest.getPin())) // Encrypt the provided PIN
                .userId(accountRequest.getUserId())
                .build();

        // Save the new account entity to the repository
        accountRepository.save(account);

        // Prepare the response DTO with the created account's details
        AccountResponse accountResponse = AccountResponse.builder()
                .accountNumber(accountNumber)
                .accountType(account.getAccountType())
                .build();

        // Return a success response with the created account's information
        return new ResponseDto<>(SUCCESS_CODE, SUCCESS, accountResponse, HttpStatus.OK);
    }

    /**
     * Retrieves the details of an account by its account number.
     *
     * @param accountNumber The account number to search for.
     * @return A ResponseDto containing the account's information if found, or an error message if not.
     */
    @Override
    public ResponseDto<?> getAccount(String accountNumber) {
        // Fetch the account based on the provided account number
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);

        // If account not found, return a failure response with an appropriate message
        if (account.isEmpty()) {
            return new ResponseDto<>(FAILED_CODE, FAILED, ACCOUNT_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        // Map the found Account entity to an AccountResponse DTO
        AccountResponse accountResponse = modelMapper.map(account.get(), AccountResponse.class);

        // Return a success response with the account's details
        return new ResponseDto<>(SUCCESS_CODE, SUCCESS, accountResponse, HttpStatus.OK);
    }
}
