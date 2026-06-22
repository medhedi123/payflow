package com.hedi.payflow.wallet.controller;

import com.hedi.payflow.wallet.dto.WalletResponse;
import com.hedi.payflow.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.hedi.payflow.wallet.dto.DepositRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/me")
    public WalletResponse getMyWallet(Authentication authentication) {
        return walletService.getMyWallet(authentication);
    }
    @PostMapping("/deposit")
    public WalletResponse deposit(
            Authentication authentication,
            @Valid @RequestBody DepositRequest request
    ) {
        return walletService.deposit(authentication, request);
    }
}