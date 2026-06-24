package com.hedi.payflow.ledger.entity;

import com.hedi.payflow.transaction.entity.WalletTransaction;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LedgerTransactionStatus status;

    @OneToOne
    @JoinColumn(name = "wallet_transaction_id")
    private WalletTransaction walletTransaction;

    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime postedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();

        if (status == null) {
            status = LedgerTransactionStatus.PENDING;
        }
    }
}