package com.payment.account.model;

import com.payment.shared.dtos.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountName;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private AccountType accountType;

    private BigDecimal balance = BigDecimal.ZERO;
    @Column(nullable = false)
    private Long userId;

    private String  pin;
}
