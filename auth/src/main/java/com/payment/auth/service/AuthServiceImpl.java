package com.payment.auth.service;

import com.payment.auth.dtos.request.RegistrationRequest;
import com.payment.auth.dtos.request.VerificationRequest;
import com.payment.auth.dtos.response.RegistrationResponse;
import com.payment.auth.dtos.response.TokenResponse;
import com.payment.auth.dtos.response.UserDetailsResponse;
import com.payment.auth.message.ErrorMessage;
import com.payment.auth.utils.Constants;
import com.payment.shared.dtos.ResponseDto;
import com.payment.auth.repository.UserRepository;
import com.payment.shared.client.AccountClient;
import com.payment.auth.model.User;
import com.payment.auth.exception.GlobalException;
import com.payment.auth.exception.ServiceUnavailableException;

import com.payment.shared.dtos.AccountRequest;
import com.payment.shared.dtos.AccountResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor

public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository; // Repository for interacting with User database
    private final PasswordEncoder passwordEncoder; // Password encoder to encrypt passwords
    private final ModelMapper modelMapper; // Model mapper for mapping entities to DTOs
    private final JwtService jwtService; // Service for JWT token generation
    private final AccountClient accountClient; // Client for interacting with external Account service

    /**
     * Generates a JWT token for the provided username.
     * @param username The username for which the token is generated.
     * @return TokenResponse containing the generated token.
     */
    @Override
    public TokenResponse generateToken(String username) {
        String token = jwtService.generateToken(username); // Generate token using the JWT service
        return TokenResponse.builder() // Return token wrapped in a response object
                .accessToken(token)
                .build();
    }

    /**
     * This method initializes default user registration when the service starts.
     * It is annotated with @Transactional to handle transactions.
     * @throws Exception In case of any error during the registration process.
     */
    @Transactional
    public void init() {
        try {
            // Default registration with constants
            register(RegistrationRequest.builder()
                    .bvn(Constants.BVN)
                    .nin(Constants.NIN)
                    .email(Constants.EMAIL)
                    .password(Constants.PASSWORD)
                    .firstName(Constants.FIRSTNAME)
                    .lastName(Constants.LASTNAME)
                    .build());
        } catch (Exception e) {
            e.printStackTrace(); // Log any exceptions that occur during initialization
        }
    }

    /**
     * Registers a new user based on the registration request data.
     * @param registrationRequest The data needed for registration.
     * @return RegistrationResponse containing details of the created account.
     */
    @Override
    @Transactional
    public RegistrationResponse register(RegistrationRequest registrationRequest) {

        // Validate the format of BVN and NIN
        if (!registrationRequest.isValid()) {
            throw new GlobalException(ErrorMessage.INVALID_BVN_NIN_FORMAT);
        }

        // Validate the password format
        if (!registrationRequest.validatePassword(registrationRequest.getPassword())) {
            throw new GlobalException(ErrorMessage.INVALID_PASSWORD_FORMAT);
        }

        log.info("count {}", userRepository.count()); // Log the count of users in the repository

        // Check if the email, NIN, or BVN already exists in the system
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new GlobalException(ErrorMessage.EMAIL_ALREADY_IN_USE);
        }
        if (userRepository.existsByNin(registrationRequest.getNin())){
            throw new GlobalException(ErrorMessage.NIN_ALREADY_EXISTS);
        }
        if (userRepository.existsByBvn(registrationRequest.getBvn())){
            throw new GlobalException(ErrorMessage.BVN_ALREADY_EXISTS);
        }

        // Create the user entity and encode the password
        String accountName = registrationRequest.getFirstName() + " " + registrationRequest.getLastName();
        User user = User.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .nin(registrationRequest.getBvn()) // Use BVN as NIN for registration
                .bvn(registrationRequest.getBvn())
                .password(passwordEncoder.encode(registrationRequest.getPassword())) // Encode password
                .build();
        user = userRepository.saveAndFlush(user); // Save user to the repository

        // Call external account service to create an account for the user
        ResponseDto<AccountResponse> accountResponse;
        try {
            accountResponse = accountClient.createAccount(
                    AccountRequest.builder()
                            .bvn(registrationRequest.getBvn())
                            .nin(registrationRequest.getNin())
                            .accountName(accountName)
                            .userId(user.getId()) // User ID from the saved user
                            .pin(registrationRequest.getPin()) // Use provided PIN for account
                            .build());
        } catch (WebClientResponseException.ServiceUnavailable e) {
            // Handle case when external account service is unavailable
            throw new ServiceUnavailableException(ErrorMessage.ACCOUNT_SERVICE_UNAVAILABLE);
        }

        // Return response with account details
        return RegistrationResponse.builder()
                .accountNumber(accountResponse.getRespBody().getAccountNumber()) // Account number from response
                .name(accountName) // Account holder's name
                .build();
    }

    /**
     * Retrieves user details by their username (email).
     * @param userDetails The user details object containing the username.
     * @return UserDetailsResponse containing the user data.
     */
    @Override
    public UserDetailsResponse getUserDetails(UserDetails userDetails) {
        // Fetch user from repository by email
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(userDetails.getUsername()));

        // Map user entity to response DTO and return it
        return modelMapper.map(user, UserDetailsResponse.class);
    }

    /**
     * Verifies the NIN of a user and updates their verification status.
     * @param userDetail The details of the authenticated user.
     * @param verificationRequest Contains verification details (e.g., NIN).
     * @return UserDetailsResponse with updated user data.
     */
    @Override
    public UserDetailsResponse verifyNin(UserDetails userDetail, VerificationRequest verificationRequest) {
        User user = userRepository.findByEmail(userDetail.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(userDetail.getUsername()));
        user.setNinVerified(true); // Set NIN verified status
        userRepository.saveAndFlush(user); // Save updated user entity

        // Return updated user details
        return modelMapper.map(user, UserDetailsResponse.class);
    }

    /**
     * Verifies the BVN of a user and updates their verification status.
     * @param userDetail The details of the authenticated user.
     * @param verificationRequest Contains verification details (e.g., BVN).
     * @return UserDetailsResponse with updated user data.
     */
    @Override
    public Object verifyBvn(UserDetails userDetail, VerificationRequest verificationRequest) {
        User user = userRepository.findByEmail(userDetail.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(userDetail.getUsername()));
        user.setBvnVerified(true); // Set BVN verified status
        userRepository.saveAndFlush(user); // Save updated user entity

        // Return updated user details
        return modelMapper.map(user, UserDetailsResponse.class);
    }
}
