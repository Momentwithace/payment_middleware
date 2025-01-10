package com.payment.auth;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Setter
@Getter
@Table(name = "users", indexes = @Index(columnList = "email"))
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;
    @Column(nullable = false, length = 15)
    private String firstName;
    @Column(nullable = false, length = 15)
    private String lastName;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdAt;
    @Column(nullable = false)
    @UpdateTimestamp
    private Date updatedAt;
    @Column(nullable = false)
    private boolean emailVerified = false;
    @Column(nullable = false, updatable = false, length = 11)
    private String nin;
    @Column(nullable = false, updatable = false, length = 11)
    private String bvn;
    private boolean isNinVerified;
    private boolean isBvnVerified;

}

