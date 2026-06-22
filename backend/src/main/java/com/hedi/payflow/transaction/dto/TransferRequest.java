package com.hedi.payflow.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest {

    @Email
    @NotBlank
    private String receiverEmail;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
}