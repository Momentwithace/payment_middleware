package com.payment.account.repository;

import com.payment.account.model.Account;
import com.payment.shared.dtos.AccountResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAllByUserId(Long id);

   Optional<Account> findByAccountNumber(String accountNumber);
}
