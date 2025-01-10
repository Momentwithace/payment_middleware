package com.payment.auth.service;


import com.payment.auth.dtos.request.RegistrationRequest;
import com.payment.auth.dtos.request.VerificationRequest;
import com.payment.auth.dtos.response.RegistrationResponse;
import com.payment.auth.dtos.response.TokenResponse;
import com.payment.auth.dtos.response.UserDetailsResponse;
import com.payment.auth.message.ErrorMessage;
import com.payment.auth.utils.Constants;
import com.payment.shared.dtos.ResponseDto;
import com.payment.auth.UserRepository;
import com.payment.shared.client.AccountClient;
import com.payment.auth.User;
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

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    private final JwtService jwtService;

    private final AccountClient accountClient;



    @Override
    public TokenResponse generateToken(String username) {
        String token = jwtService.generateToken(username);
        return TokenResponse.builder()
                .accessToken(token)
                .build();
    }
//    @PostConstruct
    @Transactional
   public void init() {
        try {
            register(RegistrationRequest.builder()
                    .bvn(Constants.BVN)
                    .nin(Constants.NIN)
                    .email(Constants.EMAIL)
                    .password(Constants.PASSWORD)
                    .firstName(Constants.FIRSTNAME)
                    .lastName(Constants.LASTNAME)
                    .build());
        }catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    @Transactional
    public RegistrationResponse register(RegistrationRequest registrationRequest) {

        if (!registrationRequest.isValid()) {
            throw new GlobalException(ErrorMessage.INVALID_BVN_NIN_FORMAT);
        }

        if (!registrationRequest.validatePassword(registrationRequest.getPassword())) {
            throw new GlobalException(ErrorMessage.INVALID_PASSWORD_FORMAT);
        }


        log.info("count {}", userRepository.count());
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new GlobalException(ErrorMessage.EMAIL_ALREADY_IN_USE);
        }
        if (userRepository.existsByNin(registrationRequest.getNin())){
            throw new GlobalException(ErrorMessage.NIN_ALREADY_EXISTS);
        }
        if (userRepository.existsByBvn(registrationRequest.getBvn())){
            throw new GlobalException(ErrorMessage.BVN_ALREADY_EXISTS);
        }


        String accountName = registrationRequest.getFirstName() + " " + registrationRequest.getLastName();
        User user = User.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .nin(registrationRequest.getBvn())
                .bvn(registrationRequest.getBvn())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .build();
        user = userRepository.saveAndFlush(user);
        ResponseDto<AccountResponse> accountResponse;
        try {

            accountResponse = accountClient.createAccount(
                    AccountRequest.builder()
                    .bvn(registrationRequest.getBvn())
                    .nin(registrationRequest.getNin())
                    .accountName(accountName)
                    .userId(user.getId())
                            .pin(registrationRequest.getPin())
                    .build());
        }catch (WebClientResponseException.ServiceUnavailable e){
            throw  new ServiceUnavailableException(ErrorMessage.ACCOUNT_SERVICE_UNAVAILABLE);
        }
        return RegistrationResponse.builder()
                .accountNumber(accountResponse.getRespBody().getAccountNumber())
                .name(accountName)
                .build();


    }

    @Override
    public UserDetailsResponse getUserDetails(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException(userDetails.getUsername()));
        return modelMapper.map(user, UserDetailsResponse.class);

    }


    @Override
    public UserDetailsResponse verifyNin(UserDetails userDetail, VerificationRequest verificationRequest) {
        User user = userRepository.findByEmail(userDetail.getUsername()).orElseThrow(() -> new UsernameNotFoundException(userDetail.getUsername()));
        user.setNinVerified(true);
        userRepository.saveAndFlush(user);
        return modelMapper.map(user, UserDetailsResponse.class);
    }

    @Override
    public Object verifyBvn(UserDetails userDetail, VerificationRequest verificationRequest) {
        User user = userRepository.findByEmail(userDetail.getUsername()).orElseThrow(() -> new UsernameNotFoundException(userDetail.getUsername()));
        user.setBvnVerified(true);
        userRepository.saveAndFlush(user);
        return modelMapper.map(user, UserDetailsResponse.class);
    }
}
