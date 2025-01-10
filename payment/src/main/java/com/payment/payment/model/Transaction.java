package com.payment.payment.model;

import lombok.*;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sourceAccountNumber;

    private String destinationAccountNumber;

    private String destinationAccountName;

    private String narration;

    private BigDecimal amount;

    private String status;

    private LocalDateTime createdAt;

    private TransactionType transactionType;

    private String  transactionId;
}
