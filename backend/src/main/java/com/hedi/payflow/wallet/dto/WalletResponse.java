package com.hedi.payflow.wallet.dto;

import com.hedi.payflow.wallet.entity.WalletStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WalletResponse {

    private Long id;
    private Long userId;
    private BigDecimal balance;
    private String currency;
    private WalletStatus status;
    private LocalDateTime createdAt;
}